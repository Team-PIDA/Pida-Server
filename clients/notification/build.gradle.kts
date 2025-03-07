dependencies {
    // Notification
    implementation(libs.bundles.openfeign)
    implementation(libs.firebase)

    implementation(project(":core:core-domain"))

    testImplementation(project(":tests:test-helper"))
}
