package com.pida.category.badge

import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.repository.MapCategoryBadgeRepository
import org.springframework.stereotype.Component

@Component
class MapCategoryBadgeFinder(
    private val mapCategoryBadgeRepository: MapCategoryBadgeRepository,
) {
    suspend fun findAllGroupedByTarget(
        targetType: MapCategoryBadgeTargetType,
        targetIds: List<Long>,
    ): Map<Long, List<MapCategoryBadge>> {
        if (targetIds.isEmpty()) return emptyMap()

        return mapCategoryBadgeRepository
            .findAllByTarget(targetType, targetIds)
            .groupBy { it.targetId }
            .mapValues { (_, badges) -> badges.map { MapCategoryBadge(it.type, it.label) } }
    }
}
