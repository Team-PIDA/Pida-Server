package com.pida.blooming

import java.time.LocalDateTime

data class Blooming(
    val id: Long,
    val status: BloomingStatus,
    val userId: Long,
    val flowerSpotId: Long?,
    val flowerEventId: Long?,
    val createdAt: LocalDateTime,
) {
    init {
        require((flowerSpotId == null) != (flowerEventId == null))
    }
}
