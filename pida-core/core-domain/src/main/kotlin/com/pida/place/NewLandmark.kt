package com.pida.place

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region

data class NewLandmark(
    val name: String,
    val address: String?,
    val x: Double,
    val y: Double,
    val region: Region,
    val category: LandmarkCategory?,
) {
    fun toLandmark(): Landmark =
        Landmark(
            id = 0L,
            name = name,
            address = address,
            pinPoint = GeoJson.Point(listOf(x, y)),
            region = region,
            category = category,
            deletedAt = null,
        )
}
