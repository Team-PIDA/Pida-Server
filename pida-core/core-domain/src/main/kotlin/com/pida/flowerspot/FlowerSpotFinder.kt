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

    suspend fun readAllByRegion(region: Region): List<FlowerSpot> = readAll().filterBy(region = region)

    suspend fun readBy(spotId: Long): FlowerSpot = flowerSpotRepository.findBy(spotId)

    suspend fun findByCondition(condition: FindFlowerSpotPolicyCondition): List<FlowerSpot> =
        when (condition) {
            FindFlowerSpotPolicyCondition.All -> readAll()
            is FindFlowerSpotPolicyCondition.ByRegion -> readAllByRegion(condition.region)
            is FindFlowerSpotPolicyCondition.ByLocation -> readAllByLocation(condition.location)
            is FindFlowerSpotPolicyCondition.ByRegionAndLocation -> readAllByLocationAndRegion(condition.region, condition.location)
        }

    suspend fun readAllByLocation(location: FlowerSpotLocation): List<FlowerSpot> = readAll().filterBy(location = location)

    suspend fun readAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot> = readAll().filterBy(region = region, location = location)

    suspend fun searchByStreetName(streetName: String): List<FlowerSpot> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = "$SEARCH_KEY:$streetName",
            typeReference = object : TypeReference<List<FlowerSpot>>() {},
        ) {
            flowerSpotRepository.findByStreetNameContaining(streetName)
        }

    private fun List<FlowerSpot>.filterBy(
        region: Region? = null,
        location: FlowerSpotLocation? = null,
    ): List<FlowerSpot> =
        asSequence()
            .filter { region == null || it.region == region }
            .filter { location == null || it.isWithin(location) }
            .toList()

    private fun FlowerSpot.isWithin(location: FlowerSpotLocation): Boolean {
        if (!location.hasBounds()) return true

        val point = pinPoint as? GeoJson.Point ?: return false
        if (point.coordinates.size < 2) return false

        val longitude = point.coordinates[0]
        val latitude = point.coordinates[1]

        return longitude > location.swLng!! &&
            longitude < location.neLng!! &&
            latitude > location.swLat!! &&
            latitude < location.neLat!!
    }
}
