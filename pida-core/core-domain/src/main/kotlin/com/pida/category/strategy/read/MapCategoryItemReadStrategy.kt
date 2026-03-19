package com.pida.category.strategy.read

import com.pida.category.CategoryLabel
import com.pida.category.item.model.MapCategoryItem
import com.pida.flowerspot.FlowerSpotLocation

interface MapCategoryItemReadStrategy {
    val categoryLabel: CategoryLabel

    suspend fun read(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem>
}
