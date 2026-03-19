package com.pida.category.item.model

import com.pida.blooming.BloomingStatus
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDate

data class MapCategoryItem(
    val id: Long,
    val name: String,
    val address: String?,
    val description: String?,
    val thumbnailUrl: String? = null,
    val geom: GeoJson? = null,
    val pinPoint: GeoJson,
    val region: Region,
    val homepageUrl: String? = null,
    val mapUrl: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val flowerSpotId: Long? = null,
    val recentlyVisitedCount: Long? = null,
    val bloomingStatus: BloomingStatus? = null,
    val badges: List<MapCategoryBadge> = emptyList(),
)
