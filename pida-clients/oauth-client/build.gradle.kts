dependencies {
    api(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.bouncycastle.bcpkix)
    implementation(libs.bundles.openfeign)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.jackson)
    runtimeOnly(libs.jjwt.impl)

    implementation(project(":pida-core:core-domain"))
}
