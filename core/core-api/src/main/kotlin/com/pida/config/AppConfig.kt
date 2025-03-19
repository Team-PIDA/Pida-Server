package com.pida.config

import com.pida.authentication.config.AuthenticationProperties
import com.pida.authentication.config.RsaKeyProperties
import com.pida.authentication.config.SwaggerUserProperties
import com.pida.authentication.storage.redis.config.AuthenticationRedisProperties
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    RsaKeyProperties::class,
    SwaggerUserProperties::class,
    AuthenticationRedisProperties::class,
    AuthenticationProperties::class,
    RedisProperties::class,
)
class AppConfig
