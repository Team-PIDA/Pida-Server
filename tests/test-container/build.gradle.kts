plugins {
    id("java-test-fixtures")
}

dependencies {
    compileOnly(libs.spring.boot.starter.test)

    testFixturesRuntimeOnly(libs.mysql.connector)
    testFixturesImplementation(libs.bundles.testcontainers.mysql)
    testFixturesImplementation(libs.test.containers.localstack)
    testFixturesImplementation(libs.bundles.aws.client)
    testFixturesImplementation(libs.bundles.aws.dynamo.db)
}
