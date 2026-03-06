dependencies {
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.sentry.spring.boot.starter.jakarta)
    implementation(project(":pida-core:core-domain"))
}
