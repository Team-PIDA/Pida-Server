package com.pida.blooming

sealed class NewBlooming {
    abstract val userId: Long
    abstract val flowerSpotId: Long
    abstract val status: BloomingStatus

    data class FlowerSpot(
        override val userId: Long,
        override val flowerSpotId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming()

    data class FlowerEvent(
        override val userId: Long,
        override val flowerSpotId: Long,
        override val status: BloomingStatus,
        val flowerEventId: Long,
    ) : NewBlooming()
}
