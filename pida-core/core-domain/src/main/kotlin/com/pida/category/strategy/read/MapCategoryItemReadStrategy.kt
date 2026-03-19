package com.pida.category.strategy.read

import com.pida.category.CategoryLabel
import com.pida.category.item.model.MapCategoryItem
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.support.geo.Region

interface MapCategoryItemReadStrategy {
    val categoryLabel: CategoryLabel

    suspend fun read(
        categoryId: Long,
        region: Region?,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem>
}
