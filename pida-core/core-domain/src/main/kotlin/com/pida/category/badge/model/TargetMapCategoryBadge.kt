package com.pida.category.badge.model

data class TargetMapCategoryBadge(
    val targetId: Long,
    val type: MapCategoryBadgeType,
    val label: String,
    val sortOrder: Int,
)
