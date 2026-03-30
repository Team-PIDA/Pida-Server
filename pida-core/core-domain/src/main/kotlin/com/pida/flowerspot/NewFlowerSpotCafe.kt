package com.pida.flowerspot

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region

data class NewFlowerSpotCafe(
    val name: String,
    val address: String?,
    val description: String?,
    val longitude: Double,
    val latitude: Double,
    val region: Region,
    val mapUrl: String?,
) {
    fun toDomain(): FlowerSpotCafe =
        FlowerSpotCafe(
            id = 0,
            name = name,
            address = address,
            description = description,
            thumbnailUrl = null,
            pinPoint = GeoJson.Point(listOf(longitude, latitude)),
            region = region,
            mapUrl = mapUrl,
            deletedAt = null,
        )
}
