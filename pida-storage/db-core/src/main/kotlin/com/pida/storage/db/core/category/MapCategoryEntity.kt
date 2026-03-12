package com.pida.storage.db.core.category

import com.pida.category.CategoryLabel
import com.pida.category.MapCategory
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "t_map_category")
class MapCategoryEntity(
    val title: String,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val categoryLabel: CategoryLabel,
    val description: String?,
) : BaseEntity() {
    fun toMapCategory(): MapCategory =
        MapCategory(
            id = id!!,
            title = title,
            categoryLabel = categoryLabel,
            description = description,
            deletedAt = deletedAt,
        )
}
