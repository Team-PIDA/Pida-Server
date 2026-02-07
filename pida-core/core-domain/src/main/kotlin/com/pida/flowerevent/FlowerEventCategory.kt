package com.pida.flowerevent

import java.time.LocalDateTime

data class FlowerEventCategory(
    val id: Long,
    val title: String,
    val description: String?,
    val deletedAt: LocalDateTime?,
)
