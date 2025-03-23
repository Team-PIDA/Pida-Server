package com.pida.storage.db.core.blooming

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingStatus
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "t_blooming")
class BloomingEntity(
    val userId: Long,
    val flowerSpotId: Long,
    val status: BloomingStatus,
) : BaseEntity() {
    fun toBlooming(): Blooming =
        Blooming(
            id = id!!,
            userId = userId,
            flowerSpotId = flowerSpotId,
            status = status,
            createdAt = createdAt,
        )
}
