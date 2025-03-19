package com.pida.flowerspot

import java.time.LocalDateTime

data class FlowerSpot(
    val id: Long,
    val address: String?,
    val streetName: String,
    val district: String?,
    val description: String?,
    val geom: GeoJson,        // LineString GeoJson
    val pinPoint: GeoJson,    // Point GeoJson
    val region: Region,
    val deletedAt: LocalDateTime?,
)
