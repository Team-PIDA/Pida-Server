package com.pida.reporter

import java.time.LocalDateTime

data class RecentReporter(
    val id: Long,
    val userId: Long,
    val flowerSpotId: Long,
    val updatedAt: LocalDateTime?,
)
