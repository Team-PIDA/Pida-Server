package com.pida.flowerevent

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDate

data class NewFlowerEvent(
    val name: String,
    val address: String?,
    val longitude: Double,
    val latitude: Double,
    val region: Region,
    val homepageUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val categoryId: Long,
) {
    fun toDomain(): FlowerEvent =
        FlowerEvent(
            id = 0,
            name = name,
            address = address,
            thumbnailUrl = null,
            pinPoint = GeoJson.Point(listOf(longitude, latitude)),
            region = region,
            homepageUrl = homepageUrl,
            startDate = startDate,
            endDate = endDate,
            categoryId = categoryId,
            deletedAt = null,
        )
}
