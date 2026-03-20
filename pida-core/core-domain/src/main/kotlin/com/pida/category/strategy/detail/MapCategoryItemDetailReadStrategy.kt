package com.pida.category.strategy.detail

import com.pida.category.CategoryLabel
import com.pida.category.item.detail.MapCategoryItemDetail

interface MapCategoryItemDetailReadStrategy {
    val categoryLabel: CategoryLabel

    suspend fun read(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail
}
