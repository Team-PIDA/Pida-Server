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

    implementation(project(":core:core-domain"))
    implementation(project(":core:auth"))

    implementation(project(":clients:aws-client"))
    implementation(project(":clients:notification"))
    implementation(project(":support:swagger"))

    runtimeOnly(project(":support:logging"))
    runtimeOnly(project(":support:monitoring"))
    runtimeOnly(project(":storage:db-core"))
    runtimeOnly(project(":storage:redis"))

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(project(":storage:db-core"))
    testImplementation(project(":storage:redis"))
    testImplementation(project(":tests:api-docs"))
    testImplementation(project(":tests:test-helper"))
    testImplementation(testFixtures(project(":tests:test-container")))
}
