package com.pida.category.item.detail

import com.pida.blooming.BloomingDetails
import com.pida.category.CategoryLabel
import com.pida.category.item.model.MapCategoryItem

data class MapCategoryItemDetail(
    val categoryId: Long,
    val categoryLabel: CategoryLabel,
    val item: MapCategoryItem,
    val bloomingDetails: BloomingDetails? = null,
)
