dependencies {
    implementation(project(":core:core-domain"))

    api(libs.spring.boot.starter.redis)
    implementation(libs.redisson)
}
