package com.pida.category

import java.time.LocalDateTime

data class MapCategory(
    val id: Long,
    val title: String,
    val categoryLabel: CategoryLabel,
    val description: String?,
    val deletedAt: LocalDateTime?,
)
