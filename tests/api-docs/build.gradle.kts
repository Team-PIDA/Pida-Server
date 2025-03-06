dependencies {
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.spring.boot.starter.test)
    api(libs.spring.restdocs.mockmvc)
    api(libs.spring.restdocs.restassured)
    api(libs.restassured.spring.mock.mvc)
    api(libs.epages.restdocs.api.spec.mock.mvc)
    api(libs.epages.restdocs.api.spec.restassured)
    api(libs.jackson.datatype.jsr310)
}
