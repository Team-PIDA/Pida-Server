package com.pida.storage.db.core.blooming

import org.springframework.data.jpa.repository.JpaRepository

interface BloomingJpaRepository : JpaRepository<BloomingEntity, Long> {
    fun findByUserIdAndFlowerSpotId(
        userId: Long,
        flowerSpotId: Long,
    ): BloomingEntity?

    fun findAllByUserId(userId: Long): List<BloomingEntity>

    fun findAllByFlowerSpotId(flowerSpotId: Long): List<BloomingEntity>
}
