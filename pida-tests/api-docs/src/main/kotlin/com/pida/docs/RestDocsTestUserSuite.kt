package com.pida.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsTestUserSuite {
    lateinit var mockMvcSpec: MockMvcRequestSpecification
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.restDocumentation = restDocumentation
    }

    protected fun given(): MockMvcRequestSpecification = mockMvcSpec

    protected fun mockController(
        controller: Any,
        argumentResolver: HandlerMethodArgumentResolver,
    ): MockMvcRequestSpecification {
        val mockMvc = createMockMvc(controller, argumentResolver)
        return RestAssuredMockMvc.given().mockMvc(mockMvc)
    }

    private fun createMockMvc(
        controller: Any,
        argumentResolver: HandlerMethodArgumentResolver,
    ): MockMvc {
        val converter = MappingJackson2HttpMessageConverter(objectMapper())
        return MockMvcBuilders
            .standaloneSetup(controller)
            .addFilter<StandaloneMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .apply<StandaloneMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .setMessageConverters(converter)
            .setCustomArgumentResolvers(argumentResolver)
            .build()
    }

    private fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .registerModules(JavaTimeModule())
}
