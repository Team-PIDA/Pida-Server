package com.pida.flowerspot

sealed class FindSpotPolicyCondition {
    data object All : FindSpotPolicyCondition()

    data class ByRegion(
        val region: Region,
    ) : FindSpotPolicyCondition()

    data class ByLocation(
        val location: FlowerSpotLocation,
    ) : FindSpotPolicyCondition()

    data class ByRegionAndLocation(
        val region: Region,
        val location: FlowerSpotLocation,
    ) : FindSpotPolicyCondition()
}
