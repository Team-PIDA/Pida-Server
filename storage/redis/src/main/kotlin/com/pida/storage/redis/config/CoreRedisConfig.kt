package com.pida.storage.redis.config
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class CoreRedisConfig(
    private val redisProperties: RedisProperties,
) {
    @Bean
    @Primary
    fun coreRedisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration(redisProperties.host, redisProperties.port))

    @Bean
    fun coreRedisTemplate(
        @Qualifier("coreRedisConnectionFactory") redisConnectionFactory: RedisConnectionFactory,
    ): RedisTemplate<*, *> =
        RedisTemplate<Any, Any>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer.UTF_8
            valueSerializer = StringRedisSerializer.UTF_8
        }
}
