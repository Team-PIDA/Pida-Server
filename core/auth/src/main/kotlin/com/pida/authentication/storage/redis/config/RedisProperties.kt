package com.pida.authentication.storage.redis.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth.datasource.redis")
data class RedisProperties(
    val port: Int,
    val host: String,
)
