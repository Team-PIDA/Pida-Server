package com.pida.flowerevent

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDate
import java.time.LocalDateTime

data class FlowerEvent(
    val id: Long,
    val name: String,
    val address: String?,
    val pinPoint: GeoJson,
    val region: Region,
    val homepageUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val categoryId: Long,
    val deletedAt: LocalDateTime?,
)
