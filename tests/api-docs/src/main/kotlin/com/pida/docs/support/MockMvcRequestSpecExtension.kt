package com.pida.docs.support

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification

object MockMvcRequestSpecExtension {
    fun MockMvcRequestSpecification.bodyAsString(entity: Any): MockMvcRequestSpecification {
        val objectMapper =
            jacksonObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .registerModules(JavaTimeModule())
        return this.body(objectMapper.writeValueAsString(entity))
    }
}
