dependencies {
    api(libs.spring.boot.starter.redis)

    implementation(libs.redisson)
    implementation(project(":pida-core:core-domain"))
}
