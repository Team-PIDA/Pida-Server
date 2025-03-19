package com.pida.authentication.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "swagger")
data class SwaggerUserProperties(
    val user: String,
    val password: String,
)
