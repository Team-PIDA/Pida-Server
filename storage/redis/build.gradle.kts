dependencies {
    implementation(project(":core:core-domain"))

    api(libs.spring.boot.starter.redis)
    // Redis
    implementation(libs.redisson)
}
