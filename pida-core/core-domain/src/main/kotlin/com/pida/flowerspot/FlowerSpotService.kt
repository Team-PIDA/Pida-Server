package com.pida.flowerspot

import com.pida.support.geo.Region
import org.springframework.stereotype.Service

@Service
class FlowerSpotService(
    private val flowerSpotFinder: FlowerSpotFinder,
) {
    suspend fun readAllFlowerSpot(
        region: Region?,
        location: FlowerSpotLocation,
    ): List<FlowerSpot> {
        val condition =
            when {
                !location.hasBounds() && region == null -> FindFlowerSpotPolicyCondition.All
                !location.hasBounds() -> FindFlowerSpotPolicyCondition.ByRegion(region!!)
                region == null -> FindFlowerSpotPolicyCondition.ByLocation(location)
                else -> FindFlowerSpotPolicyCondition.ByRegionAndLocation(region, location)
            }
        return flowerSpotFinder.findByCondition(condition)
    }

    suspend fun readOneFlowerSpot(spotId: Long): FlowerSpot = flowerSpotFinder.readBy(spotId)

    suspend fun searchFlowerSpots(query: String): List<FlowerSpot> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        return flowerSpotFinder.searchByStreetName(trimmed)
    }
}
