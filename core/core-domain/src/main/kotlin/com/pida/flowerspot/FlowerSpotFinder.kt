package com.pida.flowerspot

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.CacheAdvice
import org.springframework.stereotype.Component

@Component
class FlowerSpotFinder(
    private val flowerSpotRepository: FlowerSpotRepository,
    private val cacheAdvice: CacheAdvice,
) {
    companion object {
        val ALL_SPOT = "spot:all"
    }

    suspend fun readAll(): List<FlowerSpot> =
        cacheAdvice.invoke(
            ttl = 1440L,
            key = ALL_SPOT,
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            flowerSpotRepository.findAll()
        }
}
