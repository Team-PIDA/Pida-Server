package com.pida.storage.redis.config
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "core.storage.redis")
data class RedisProperties(
    val host: String,
    val port: Int,
)
