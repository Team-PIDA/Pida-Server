package com.pida.flowerspot

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDateTime

data class FlowerSpotCafe(
    val id: Long,
    val flowerSpotId: Long,
    val name: String,
    val address: String?,
    val description: String?,
    val pinPoint: GeoJson,
    val region: Region,
    val mapUrl: String?,
    val deletedAt: LocalDateTime?,
)
