package com.pida.blooming

import java.time.LocalDateTime

data class Blooming(
    val id: Long,
    val status: BloomingStatus,
    val userId: Long,
    val flowerSpotId: Long,
    val createdAt: LocalDateTime,
)
