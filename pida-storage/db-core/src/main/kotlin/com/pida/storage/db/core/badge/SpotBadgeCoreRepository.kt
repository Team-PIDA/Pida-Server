package com.pida.storage.db.core.badge

import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.TargetMapCategoryBadge
import com.pida.category.badge.repository.MapCategoryBadgeRepository
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class SpotBadgeCoreRepository(
    private val spotBadgeJpaRepository: SpotBadgeJpaRepository,
    private val tx: TransactionTemplates,
) : MapCategoryBadgeRepository {
    override suspend fun findAllByTarget(
        targetType: MapCategoryBadgeTargetType,
        targetIds: List<Long>,
    ): List<TargetMapCategoryBadge> =
        tx.reader.coExecute {
            if (targetIds.isEmpty()) {
                emptyList()
            } else {
                spotBadgeJpaRepository
                    .findByTargetTypeAndTargetIdInAndDeletedAtIsNullOrderByTargetIdAscSortOrderAscIdAsc(
                        targetType = targetType,
                        targetIds = targetIds,
                    ).map { it.toTargetMapCategoryBadge() }
            }
        }
}
