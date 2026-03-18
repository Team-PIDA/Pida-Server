package com.pida.storage.db.core.category

import org.springframework.data.jpa.repository.JpaRepository

interface MapCategoryJpaRepository : JpaRepository<MapCategoryEntity, Long> {
    fun findByDeletedAtIsNullOrderByIdAsc(): List<MapCategoryEntity>
}
