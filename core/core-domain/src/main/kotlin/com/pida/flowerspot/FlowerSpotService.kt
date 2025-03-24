package com.pida.flowerspot

import org.springframework.stereotype.Service

@Service
class FlowerSpotService(
    private val flowerSpotFinder: FlowerSpotFinder,
) {
    suspend fun findAllFlowerSpot(
        region: Region?,
        location: FlowerSpotLocation,
    ): List<FlowerSpot> {
        val condition =
            when {
                !location.isNotSet() && region == null -> FindSpotPolicyCondition.All
                !location.isNotSet() -> FindSpotPolicyCondition.ByRegion(region!!)
                region == null -> FindSpotPolicyCondition.ByLocation(location)
                else -> FindSpotPolicyCondition.ByRegionAndLocation(region, location)
            }
        return flowerSpotFinder.findByCondition(condition)
    }

    suspend fun findOneFlowerSpot(spotId: Long): FlowerSpot = flowerSpotFinder.readBy(spotId)
}
