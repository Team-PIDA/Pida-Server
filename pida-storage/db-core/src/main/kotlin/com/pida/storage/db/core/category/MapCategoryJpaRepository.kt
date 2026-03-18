package com.pida.storage.db.core.category

import com.pida.category.CategoryLabel
import org.springframework.data.jpa.repository.JpaRepository

interface MapCategoryJpaRepository : JpaRepository<MapCategoryEntity, Long> {
    fun findByDeletedAtIsNullOrderByIdAsc(): List<MapCategoryEntity>

    fun findByCategoryLabelAndDeletedAtIsNull(categoryLabel: CategoryLabel): List<MapCategoryEntity>
}
