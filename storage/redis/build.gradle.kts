dependencies {
    api(libs.spring.boot.starter.redis)

    implementation(libs.redisson)
    implementation(project(":core:core-domain"))
}
