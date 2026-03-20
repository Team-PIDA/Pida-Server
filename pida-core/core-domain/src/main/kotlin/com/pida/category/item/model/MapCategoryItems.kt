package com.pida.category.item.model

import com.pida.category.CategoryLabel

data class MapCategoryItems(
    val categoryId: Long,
    val categoryLabel: CategoryLabel,
    val list: List<MapCategoryItem>,
)
