package com.pida.client.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.pida.client.oauth.response.ApplePublicKeysResponse
import com.pida.support.cache.Cache
import com.pida.support.cache.CacheRepository
import org.springframework.stereotype.Component

@Component
class ApplePublicKeysCache(
    private val cacheRepository: CacheRepository,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private const val CACHE_KEY = "apple:auth:public-keys"
        private const val CACHE_TTL_MINUTES = 360L
    }

    fun getOrLoad(loader: () -> ApplePublicKeysResponse): ApplePublicKeysResponse =
        Cache.cacheBlocking(
            ttl = CACHE_TTL_MINUTES,
            key = CACHE_KEY,
            typeReference = object : com.fasterxml.jackson.core.type.TypeReference<ApplePublicKeysResponse>() {},
        ) {
            loader()
        }

    fun getCachedOrNull(): ApplePublicKeysResponse? {
        val cached = cacheRepository.get(CACHE_KEY) ?: return null
        return objectMapper.readValue(cached, ApplePublicKeysResponse::class.java)
    }
}
