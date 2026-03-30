package com.pida.blooming

import java.time.LocalDateTime

data class Blooming(
    val id: Long,
    val status: BloomingStatus,
    val userId: Long,
    val flowerSpotId: Long?,
    val flowerEventId: Long?,
    val flowerSpotCafeId: Long?,
    val createdAt: LocalDateTime,
) {
    init {
        val nonNullCount = listOfNotNull(flowerSpotId, flowerEventId, flowerSpotCafeId).size
        require(nonNullCount == 1)
    }
}
