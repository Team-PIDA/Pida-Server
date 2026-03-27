package com.pida.blooming

sealed class NewBlooming {
    abstract val userId: Long
    abstract val status: BloomingStatus
    abstract val flowerSpotId: Long?
    abstract val flowerEventId: Long?
    abstract val flowerSpotCafeId: Long?

    data class FlowerSpot(
        override val userId: Long,
        override val flowerSpotId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming() {
        override val flowerEventId: Long? = null
        override val flowerSpotCafeId: Long? = null
    }

    data class FlowerEvent(
        override val userId: Long,
        override val flowerEventId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming() {
        override val flowerSpotId: Long? = null
        override val flowerSpotCafeId: Long? = null
    }

    data class FlowerSpotCafe(
        override val userId: Long,
        override val flowerSpotCafeId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming() {
        override val flowerSpotId: Long? = null
        override val flowerEventId: Long? = null
    }
}
