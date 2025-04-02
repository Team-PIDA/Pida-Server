dependencies {
    // Notification
    implementation(libs.bundles.openfeign)
    implementation(libs.firebase)

    implementation(project(":pida-core:core-domain"))

    testImplementation(project(":pida-tests:test-helper"))
}
