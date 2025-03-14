import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.kapt)
	alias(libs.plugins.kotlin.spring) apply false
	alias(libs.plugins.ktlint) apply false
	alias(libs.plugins.spring.boot) apply false
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.kotlin.jpa) apply false
	alias(libs.plugins.asciidoctor.convert) apply false
	alias(libs.plugins.epages.restdocs.api.spec) apply false
	alias(libs.plugins.hidetake.swagger.generator) apply false
}

allprojects {
	group = property("projectGroup").toString()
	version = property("applicationVersion").toString()
}

subprojects {
    val libs = rootProject.libs
    val asciidoctorExt: Configuration by configurations.creating
    fun getPlugin(provider: Provider<PluginDependency>): String = provider.get().pluginId

    apply(plugin = getPlugin(libs.plugins.kotlin.jvm))
    apply(plugin = getPlugin(libs.plugins.kotlin.kapt))
    apply(plugin = getPlugin(libs.plugins.kotlin.spring))
    apply(plugin = getPlugin(libs.plugins.ktlint))
    apply(plugin = getPlugin(libs.plugins.kotlin.jpa))
    apply(plugin = getPlugin(libs.plugins.spring.boot))
    apply(plugin = getPlugin(libs.plugins.spring.dependency.management))
    apply(plugin = getPlugin(libs.plugins.asciidoctor.convert))
    apply(plugin = getPlugin(libs.plugins.epages.restdocs.api.spec))
    apply(plugin = getPlugin(libs.plugins.hidetake.swagger.generator))

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        implementation(libs.kotlin.reflect)
        implementation(libs.kotlin.stdlib.jdk8)
        implementation(libs.jackson.kotlin)

        annotationProcessor(libs.spring.boot.configuration.processor)
        kapt(libs.spring.boot.configuration.processor)

        testImplementation(libs.spring.mockk)
        testImplementation(libs.bundles.kotest)
        testImplementation(libs.spring.boot.starter.test)
//        testImplementation(libs.spring.security.test)
    }

    tasks.withType<KotlinCompile> {
        kotlin {
            compilerOptions {
                freeCompilerArgs.set(listOf("-Xjsr305=strict"))
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    // ktlint 설정 추가
    configure<KtlintExtension> {
        version.set(libs.versions.ktlint.version.set)
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    tasks.getByName("jar") {
        enabled = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.test {
        useJUnitPlatform {
            excludeTags("develop", "restdocs")
        }
    }

    tasks.register<Test>("unitTest") {
        group = "verification"
        useJUnitPlatform {
            excludeTags("develop", "context", "restdocs")
        }
    }

    tasks.register<Test>("developTest") {
        group = "verification"
        useJUnitPlatform {
            includeTags("develop")
        }
    }

    tasks.register<Test>("contextTest") {
        group = "verification"
        useJUnitPlatform {
            includeTags("context")
        }
    }

    tasks.register<Test>("restDocsTest") {
        group = "verification"
        useJUnitPlatform {
            includeTags("restdocs")
        }
    }

    tasks.register<Copy>("copyOasSwagger") {
        dependsOn("openapi3")
        doFirst {
            delete("${project.property("openapi3IntoDirectory")}/${project.property("openapi3JsonName")}.yaml")

            val jwtSchemes = "  securitySchemes:\n" +
                    "    Authorization:\n" +
                    "      type: http\n" +
                    "      scheme: bearer\n" +
                    "      bearerFormat: JWT\n" +
                    "security:\n" +
                    "  - Authorization: []"
            file("${project.property("openapi3OutDirectory")}/${project.property("openapi3JsonName")}.yaml")
                .appendText(jwtSchemes)
        }
        from("${project.property("openapi3OutDirectory")}/${project.property("openapi3JsonName")}.yaml")
        into("${project.property("openapi3IntoDirectory")}")
    }

    tasks.getByName("asciidoctor") {
        dependsOn("restDocsTest")
    }
}


tasks.register("addLintPreCommitHook", DefaultTask::class) {
    group = "setup"
    description = "Install git hooks"
    doLast {
        val hooksDir = project.file(".git/hooks")
        val scriptDir = project.file(".githooks")
        val preCommit = scriptDir.resolve("pre-commit")
        Runtime.getRuntime().exec("chmod +x .git/hooks/pre-commit")
        preCommit.copyTo(hooksDir.resolve("pre-commit"), overwrite = true)
        hooksDir.resolve("pre-commit").setExecutable(true)
    }
}

tasks.named("compileKotlin") {
    dependsOn("addLintPreCommitHook")
}
