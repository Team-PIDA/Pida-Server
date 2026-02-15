plugins {
    id("io.sentry.jvm.gradle")
}

sentry {
    includeSourceContext.set(true)
    org.set("pida-za")
    projectName.set("java-spring-boot")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}

tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.aop)
    implementation(libs.spring.boot.starter.validation)

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
