package com.partimestudy.support.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class Cache(
    _cacheAdvice: CacheAdvice,
) {
    init {
        cacheAdvice = _cacheAdvice
    }
    companion object {
        private lateinit var cacheAdvice: CacheAdvice
        fun <T> cache(
            ttl: Long,
            key: String,
            typeReference: TypeReference<T>,
            function: () -> T,
        ): T {
            return cacheAdvice.invoke(ttl, key, typeReference, function)
        }
    }
}

@Component
class CacheAdvice(
    private val cacheRepository: CacheRepository,
    private val objectMapper: ObjectMapper,
) {
    fun <T> invoke(
        ttl: Long,
        key: String,
        typeReference: TypeReference<T>,
        function: () -> T,
    ): T {
        val cached = cacheRepository.get(key)
        if (cached != null) {
            return objectMapper.readValue(cached, typeReference)
        }

        val result = function()

        if (result != null) {
            val serialized = objectMapper.writeValueAsString(result)
            cacheRepository.put(key, serialized, ttl)
        }
        return result
    }
}
