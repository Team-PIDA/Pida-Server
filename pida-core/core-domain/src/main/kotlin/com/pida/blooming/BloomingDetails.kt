package com.pida.blooming

import java.time.LocalDateTime

data class BloomingDetails(
    val totalCount: Long,
    val nickname: String?,
    val updatedAt: LocalDateTime?,
    val details: Map<String, Map<String, BloomingStatusDetails>>,
)
