package com.pida.category

interface MapCategoryItemDetailReadStrategy {
    val categoryLabel: CategoryLabel

    suspend fun read(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail
}
