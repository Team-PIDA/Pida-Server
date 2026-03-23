package com.pida.storage.db.core.blooming

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingStatus
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "t_blooming",
    indexes = [
        Index(name = "idx_blooming_flower_spot_id_created_at", columnList = "flower_spot_id,created_at"),
        Index(name = "idx_blooming_flower_spot_id_status_created_at", columnList = "flower_spot_id,status,created_at"),
    ],
)
class BloomingEntity(
    val userId: Long,
    val flowerSpotId: Long?,
    val flowerEventId: Long?,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val status: BloomingStatus,
) : BaseEntity() {
    fun toBlooming(): Blooming =
        Blooming(
            id = id!!,
            userId = userId,
            flowerSpotId = flowerSpotId,
            flowerEventId = flowerEventId,
            status = status,
            createdAt = createdAt,
        )
}
