package com.pida.blooming

sealed class NewBlooming {
    abstract val userId: Long
    abstract val status: BloomingStatus
    abstract val flowerSpotId: Long?
    abstract val flowerEventId: Long?

    data class FlowerSpot(
        override val userId: Long,
        override val flowerSpotId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming() {
        override val flowerEventId: Long? = null
    }

    data class FlowerEvent(
        override val userId: Long,
        override val flowerEventId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming() {
        override val flowerSpotId: Long? = null
    }
}
