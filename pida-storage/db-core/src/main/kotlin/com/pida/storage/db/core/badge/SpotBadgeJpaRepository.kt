package com.pida.storage.db.core.badge

import com.pida.category.badge.model.MapCategoryBadgeTargetType
import org.springframework.data.jpa.repository.JpaRepository

interface SpotBadgeJpaRepository : JpaRepository<SpotBadgeEntity, Long> {
    fun findByTargetTypeAndTargetIdInAndDeletedAtIsNullOrderByTargetIdAscSortOrderAscIdAsc(
        targetType: MapCategoryBadgeTargetType,
        targetIds: List<Long>,
    ): List<SpotBadgeEntity>
}
