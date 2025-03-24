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
            ttl = 180L,
            key = ALL_SPOT,
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            flowerSpotRepository.findAll()
        }

    suspend fun readAllByRegion(region: Region): List<FlowerSpot> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = ALL_SPOT + ":${region.name}",
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            flowerSpotRepository.findAllByRegion(region)
        }

    suspend fun readBy(spotId: Long): FlowerSpot = flowerSpotRepository.findBy(spotId)

    suspend fun findByCondition(condition: FindSpotPolicyCondition): List<FlowerSpot> =
        when (condition) {
            FindSpotPolicyCondition.All -> readAll()
            is FindSpotPolicyCondition.ByRegion -> readAllByRegion(condition.region)
            is FindSpotPolicyCondition.ByLocation -> readAllByLocation(condition.location)
            is FindSpotPolicyCondition.ByRegionAndLocation -> readAllByLocationAndRegion(condition.region, condition.location)
        }

    suspend fun readAllByLocation(location: FlowerSpotLocation): List<FlowerSpot> = flowerSpotRepository.findAllByLocation(location)

    suspend fun readAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot> = flowerSpotRepository.findAllByLocationAndRegion(region, location)
}
