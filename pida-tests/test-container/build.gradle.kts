plugins {
    id("java-test-fixtures")
}

dependencies {
    compileOnly(libs.spring.boot.starter.test)

    testFixturesRuntimeOnly(libs.postgresql.connector)
    testFixturesImplementation(libs.bundles.testcontainers.postgres)
    testFixturesImplementation(libs.test.containers.localstack)
    testFixturesImplementation(libs.bundles.aws.client)
}
