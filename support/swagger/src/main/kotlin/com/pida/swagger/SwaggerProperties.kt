package com.pida.swagger

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "swagger")
data class SwaggerProperties(
    val user: String,
    val password: String,
    val domain: String,
)
