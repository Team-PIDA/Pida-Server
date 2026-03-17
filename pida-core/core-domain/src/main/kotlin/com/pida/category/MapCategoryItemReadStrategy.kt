package com.pida.category

import com.pida.flowerspot.FlowerSpotLocation

interface MapCategoryItemReadStrategy {
    val categoryLabel: CategoryLabel

    suspend fun read(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem>
}
