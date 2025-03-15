allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    api(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.redis)
    implementation(libs.redisson)
    implementation(libs.spring.boot.starter.web)

    // Security
    implementation(libs.spring.boot.starter.security)
    testImplementation(libs.spring.security.test)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.jackson)
    runtimeOnly(libs.jjwt.impl)

    implementation(libs.bundles.openfeign)
    implementation(project(":support:swagger"))
    implementation(project(":support:logging"))

    runtimeOnly(libs.postgresql.connector)
    runtimeOnly(libs.h2)

    testImplementation(project(":tests:test-helper"))
    testImplementation(testFixtures(project(":tests:test-container")))
    testImplementation(libs.spring.boot.starter.test)
}
