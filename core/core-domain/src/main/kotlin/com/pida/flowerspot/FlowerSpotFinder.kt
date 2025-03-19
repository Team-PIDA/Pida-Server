package com.pida.flowerspot

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.Cache
import org.springframework.stereotype.Component

@Component
class FlowerSpotFinder(
    private val flowerSpotRepository: FlowerSpotRepository,
) {
    companion object {
        val ALL_SPOT = "spot:all"
    }

    fun readAll(): List<FlowerSpot> =
        Cache.cache(
            ttl = 1440L,
            key = ALL_SPOT,
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            return@cache flowerSpotRepository.findAll()
        }
}
