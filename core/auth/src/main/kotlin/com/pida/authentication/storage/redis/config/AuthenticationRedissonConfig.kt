package com.pida.authentication.storage.redis.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationRedissonConfig(
    private val redisProperties: RedisProperties,
) {
    @Bean
    fun authRedissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("redis://" + redisProperties.host + ":" + redisProperties.port)
        return Redisson.create(config)
    }
}
