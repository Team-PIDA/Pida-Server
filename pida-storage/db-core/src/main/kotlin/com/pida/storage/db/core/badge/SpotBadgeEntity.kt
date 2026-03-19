package com.pida.storage.db.core.badge

import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.badge.model.TargetMapCategoryBadge
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "t_spot_badge",
    indexes = [
        Index(
            name = "idx_spot_badge_target_type_target_id_deleted_at",
            columnList = "target_type,target_id,deleted_at",
        ),
    ],
)
class SpotBadgeEntity(
    @Enumerated(value = EnumType.STRING)
    @Column(name = "target_type", columnDefinition = "varchar(50)")
    val targetType: MapCategoryBadgeTargetType,
    @Column(name = "target_id")
    val targetId: Long,
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", columnDefinition = "varchar(30)")
    val type: MapCategoryBadgeType,
    @Column(name = "label")
    val label: String,
    @Column(name = "sort_order")
    val sortOrder: Int = 0,
) : BaseEntity() {
    fun toTargetMapCategoryBadge(): TargetMapCategoryBadge =
        TargetMapCategoryBadge(
            targetId = targetId,
            type = type,
            label = label,
            sortOrder = sortOrder,
        )
}
