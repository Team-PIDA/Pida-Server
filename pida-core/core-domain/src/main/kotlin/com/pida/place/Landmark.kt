package com.pida.place

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDateTime

data class Landmark(
    val id: Long,
    val name: String,
    val address: String?,
    val pinPoint: GeoJson, // Point GeoJson
    val region: Region,
    val deletedAt: LocalDateTime?,
)
