dependencies {
    compileOnly(libs.spring.context)
    implementation(libs.spring.tx)
    implementation(libs.slf4j)
    implementation(libs.jakarta.annotation.api)

    // Coroutine
    implementation(libs.kotlinx.coroutine.core)
    implementation(libs.kotlinx.coroutine.reactor)
    implementation(libs.kotlinx.coroutine.slf4j)

    implementation(libs.reactor.kotlin)

    // Arrow Kt
    implementation(libs.arrow.fx.coroutine)
    implementation(libs.arrow.fx.stm)
}
