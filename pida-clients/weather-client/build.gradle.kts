dependencies {
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.bundles.jackson)

    implementation(project(":pida-core:core-domain"))
}
