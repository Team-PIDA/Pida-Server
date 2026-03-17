package com.pida.category

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDate

data class MapCategoryItems(
    val categoryId: Long,
    val categoryLabel: CategoryLabel,
    val list: List<MapCategoryItem>,
)

data class MapCategoryItem(
    val id: Long,
    val name: String,
    val address: String?,
    val description: String?,
    val pinPoint: GeoJson,
    val region: Region,
    val homepageUrl: String? = null,
    val mapUrl: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val flowerSpotId: Long? = null,
)
