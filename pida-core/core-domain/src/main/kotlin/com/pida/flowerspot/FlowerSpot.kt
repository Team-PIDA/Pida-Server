package com.pida.flowerspot

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDateTime

data class FlowerSpot(
    val id: Long,
    val address: String?,
    val streetName: String,
    val district: String?,
    val description: String?,
    val geom: GeoJson, // LineString GeoJson
    val pinPoint: GeoJson, // Point GeoJson
    val region: Region,
    val kind: FlowerKind,
    val type: FlowerSpotType,
    val deletedAt: LocalDateTime?,
    val previewImageKey: String? = null,
    val previewImageUploadedAt: LocalDateTime? = null,
)
