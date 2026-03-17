package com.pida.storage.db.core.flowerevent

import org.springframework.data.jpa.repository.JpaRepository

interface FlowerEventJpaRepository : JpaRepository<FlowerEventEntity, Long> {
    fun findByCategoryIdAndDeletedAtIsNullOrderByStartDateAscIdAsc(categoryId: Long): List<FlowerEventEntity>
}
