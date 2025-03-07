dependencies {
    compileOnly(libs.spring.boot.starter.web)

    implementation(libs.bundles.aws.client)
    implementation(project(":core:core-domain"))

    testImplementation(libs.spring.boot.starter.web)
    testImplementation(testFixtures(project(":tests:test-container")))

    // AWS
    implementation(libs.aws.sdk.s3)
}
