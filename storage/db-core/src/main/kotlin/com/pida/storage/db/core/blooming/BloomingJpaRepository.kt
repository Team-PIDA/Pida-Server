package com.pida.storage.db.core.blooming

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface BloomingJpaRepository :
    JpaRepository<BloomingEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByUserIdAndFlowerSpotId(
        userId: Long,
        flowerSpotId: Long,
    ): BloomingEntity?

    fun findAllByUserId(userId: Long): List<BloomingEntity>

    fun findAllByFlowerSpotId(flowerSpotId: Long): List<BloomingEntity>
}
