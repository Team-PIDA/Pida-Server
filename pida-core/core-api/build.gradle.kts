plugins {
    id("io.sentry.jvm.gradle")
}

val hasSentryToken = System.getenv("SENTRY_AUTH_TOKEN") != null

sentry {
    includeSourceContext.set(hasSentryToken)
    org.set("pida-za")
    projectName.set("pida")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}

val sentryAgent: Configuration by configurations.creating

tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

tasks.register<Copy>("copySentryAgent") {
    from(sentryAgent)
    into(layout.buildDirectory.dir("agent"))
    rename { "sentry-opentelemetry-agent.jar" }
}

tasks.named("build") {
    dependsOn("copySentryAgent")
}

dependencies {
    sentryAgent(libs.sentry.opentelemetry.agent)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.aop)
    implementation(libs.spring.boot.starter.validation)
    compileOnly(libs.redisson)

    // Security
    implementation(libs.spring.boot.starter.security)
    testImplementation(libs.spring.security.test)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.jackson)
    runtimeOnly(libs.jjwt.impl)

    implementation(project(":pida-core:core-domain"))
    implementation(project(":pida-clients:airquality-client"))
    implementation(project(":pida-clients:aws-client"))
    implementation(project(":pida-clients:notification"))
    implementation(project(":pida-clients:oauth-client"))
    implementation(project(":pida-clients:weather-client"))
    implementation(project(":pida-clients:map-client"))
    implementation(project(":pida-supports:swagger"))

    runtimeOnly(project(":pida-supports:logging"))
    runtimeOnly(project(":pida-supports:monitoring"))
    runtimeOnly(project(":pida-storage:db-core"))
    runtimeOnly(project(":pida-storage:redis"))

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(project(":pida-storage:db-core"))
    testImplementation(project(":pida-storage:redis"))
    testImplementation(project(":pida-tests:api-docs"))
    testImplementation(project(":pida-tests:test-helper"))
    testImplementation(testFixtures(project(":pida-tests:test-container")))
}
