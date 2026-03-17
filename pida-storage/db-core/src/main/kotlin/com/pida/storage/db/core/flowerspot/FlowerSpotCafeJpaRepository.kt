package com.pida.storage.db.core.flowerspot

import org.springframework.data.jpa.repository.JpaRepository

interface FlowerSpotCafeJpaRepository : JpaRepository<FlowerSpotCafeEntity, Long> {
    fun findByDeletedAtIsNullOrderByIdAsc(): List<FlowerSpotCafeEntity>

    fun findByFlowerSpotIdAndDeletedAtIsNull(flowerSpotId: Long): List<FlowerSpotCafeEntity>
}
