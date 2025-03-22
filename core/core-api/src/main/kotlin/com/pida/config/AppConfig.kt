package com.pida.config

import com.pida.authentication.config.AuthenticationProperties
import com.pida.authentication.config.RsaKeyProperties
import com.pida.authentication.storage.redis.config.AuthenticationRedisProperties
import com.pida.swagger.SwaggerProperties
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    RsaKeyProperties::class,
    SwaggerProperties::class,
    AuthenticationRedisProperties::class,
    AuthenticationProperties::class,
    RedisProperties::class,
)
class AppConfig
