package com.pida.flowerspot

sealed class FindFlowerSpotPolicyCondition {
    data object All : FindFlowerSpotPolicyCondition()

    data class ByRegion(
        val region: Region,
    ) : FindFlowerSpotPolicyCondition()

    data class ByLocation(
        val location: FlowerSpotLocation,
    ) : FindFlowerSpotPolicyCondition()

    data class ByRegionAndLocation(
        val region: Region,
        val location: FlowerSpotLocation,
    ) : FindFlowerSpotPolicyCondition()
}
