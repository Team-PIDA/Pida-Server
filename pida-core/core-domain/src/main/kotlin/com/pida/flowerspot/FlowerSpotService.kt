package com.pida.flowerspot

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
                !location.isNotSet() && region == null -> FindFlowerSpotPolicyCondition.All
                !location.isNotSet() -> FindFlowerSpotPolicyCondition.ByRegion(region!!)
                region == null -> FindFlowerSpotPolicyCondition.ByLocation(location)
                else -> FindFlowerSpotPolicyCondition.ByRegionAndLocation(region, location)
            }
        return flowerSpotFinder.findByCondition(condition)
    }

    suspend fun readOneFlowerSpot(spotId: Long): FlowerSpot = flowerSpotFinder.readBy(spotId)
}
