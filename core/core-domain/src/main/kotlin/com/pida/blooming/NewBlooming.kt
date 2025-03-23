package com.pida.blooming

data class NewBlooming(
    val userId: Long,
    val flowerSpotId: Long,
    val status: BloomingStatus,
)
