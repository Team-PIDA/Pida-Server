package com.pida.storage.db.core.flowerevent

import com.pida.flowerevent.FlowerEventCategory
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "t_flower_event_category")
class FlowerEventCategoryEntity(
    val title: String,
    val description: String?,
) : BaseEntity() {
    fun toFlowerEventCategory(): FlowerEventCategory =
        FlowerEventCategory(
            id = id!!,
            title = title,
            description = description,
            deletedAt = deletedAt,
        )
}
