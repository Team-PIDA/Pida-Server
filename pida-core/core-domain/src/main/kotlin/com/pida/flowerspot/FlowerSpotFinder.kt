package com.pida.flowerspot

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.CacheAdvice
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import org.springframework.stereotype.Component

@Component
class FlowerSpotFinder(
    private val flowerSpotRepository: FlowerSpotRepository,
    private val cacheAdvice: CacheAdvice,
) {
    companion object {
        const val ALL_SPOT = "spot:all"
        const val SEARCH_KEY = "spot:search"
    }

    suspend fun readAll(): List<FlowerSpot> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = ALL_SPOT,
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            flowerSpotRepository.findAll()
        }

    suspend fun readAllByRegion(region: Region): List<FlowerSpot> = readAll().filter { it.region == region }

    suspend fun readBy(spotId: Long): FlowerSpot = flowerSpotRepository.findBy(spotId)

    suspend fun findByCondition(condition: FindFlowerSpotPolicyCondition): List<FlowerSpot> =
        when (condition) {
            FindFlowerSpotPolicyCondition.All -> readAll()
            is FindFlowerSpotPolicyCondition.ByRegion -> readAllByRegion(condition.region)
            is FindFlowerSpotPolicyCondition.ByLocation -> readAllByLocation(condition.location)
            is FindFlowerSpotPolicyCondition.ByRegionAndLocation -> readAllByLocationAndRegion(condition.region, condition.location)
        }

    suspend fun readAllByLocation(location: FlowerSpotLocation): List<FlowerSpot> = readAll().filterByBounds(location)

    suspend fun readAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot> = readAll().filter { it.region == region }.filterByBounds(location)

    private fun List<FlowerSpot>.filterByBounds(location: FlowerSpotLocation): List<FlowerSpot> {
        val swLat = location.swLat ?: return this
        val swLng = location.swLng ?: return this
        val neLat = location.neLat ?: return this
        val neLng = location.neLng ?: return this

        return filter { spot ->
            val point = spot.pinPoint as? GeoJson.Point ?: return@filter false
            val lng = point.coordinates[0]
            val lat = point.coordinates[1]
            lat in swLat..neLat && lng in swLng..neLng
        }
    }

    suspend fun searchByStreetName(streetName: String): List<FlowerSpot> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = "$SEARCH_KEY:$streetName",
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            flowerSpotRepository.findByStreetNameContaining(streetName)
        }
}
