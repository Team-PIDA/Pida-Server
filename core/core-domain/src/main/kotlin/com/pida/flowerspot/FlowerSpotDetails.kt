package com.pida.flowerspot

import java.time.LocalDateTime

data class FlowerSpotDetails(
    val id: Long,
    val address: String?,
    val recentlyVisitedCount: Long,
    val streetName: String,
    val district: String?,
    val description: String?,
    val geom: GeoJson, // LineString GeoJson
    val pinPoint: GeoJson, // Point GeoJson
    val region: Region,
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun of(
            flowerSpot: FlowerSpot,
            recentlyVisitedCount: Long,
        ) = FlowerSpotDetails(
            id = flowerSpot.id,
            address = flowerSpot.address,
            recentlyVisitedCount = recentlyVisitedCount,
            streetName = flowerSpot.streetName,
            district = flowerSpot.district,
            description = flowerSpot.description,
            geom = flowerSpot.geom,
            pinPoint = flowerSpot.pinPoint,
            region = flowerSpot.region,
            deletedAt = flowerSpot.deletedAt,
        )
    }
}
