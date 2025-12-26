dependencies {
    compileOnly(libs.spring.boot.starter.web)

    implementation(libs.bundles.aws.client)
    implementation(project(":pida-core:core-domain"))

    testImplementation(libs.spring.boot.starter.web)
    testImplementation(testFixtures(project(":pida-tests:test-container")))
}
