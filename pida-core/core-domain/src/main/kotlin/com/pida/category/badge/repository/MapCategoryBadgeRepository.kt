package com.pida.category.badge.repository

import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.TargetMapCategoryBadge

interface MapCategoryBadgeRepository {
    suspend fun findAllByTarget(
        targetType: MapCategoryBadgeTargetType,
        targetIds: List<Long>,
    ): List<TargetMapCategoryBadge>
}
