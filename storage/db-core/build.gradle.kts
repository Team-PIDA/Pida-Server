allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.bundles.line.kotlin.jdsl)
    implementation(libs.hibernate.spatial)
    compileOnly(project(":core:core-domain"))

    runtimeOnly(libs.mysql.connector)
    runtimeOnly(libs.h2)

    testImplementation(project(":core:core-domain"))
    testImplementation(project(":tests:test-helper"))
    testImplementation(testFixtures(project(":tests:test-container")))
}
