package com.pida.storage.redis.support

import com.pida.support.cache.CacheRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class CacheCoreRepository(
    @Qualifier("coreRedisTemplate")
    private val redisTemplate: RedisTemplate<String, String>,
) : CacheRepository {
    override fun get(key: String): String? = redisTemplate.opsForValue().get(key)

    override fun put(
        key: String,
        value: String,
        ttl: Long,
    ) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MINUTES)
    }

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }
}
