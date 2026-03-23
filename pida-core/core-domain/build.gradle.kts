import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer

dependencies {
    compileOnly(libs.spring.context)
    implementation(libs.spring.tx)
    implementation(libs.slf4j)
    implementation(libs.jakarta.annotation.api)

    // Coroutine
    implementation(libs.kotlinx.coroutine.core)
    implementation(libs.kotlinx.coroutine.reactor)
    implementation(libs.kotlinx.coroutine.slf4j)

    implementation(libs.reactor.kotlin)

    // Arrow Kt
    implementation(libs.arrow.fx.coroutine)
    implementation(libs.arrow.fx.stm)

    // Bucket4j
    implementation(libs.bucket4j.core)

    // Caffeine
    implementation(libs.caffeine)
}

val sourceSets = the<SourceSetContainer>()

tasks.register<JavaExec>("flowerSpotPerformanceBenchmark") {
    group = "verification"
    description = "Run flower-spot synthetic latency benchmark against the legacy implementation"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.pida.flowerspot.perf.FlowerSpotPerformanceBenchmarkRunner")
}
