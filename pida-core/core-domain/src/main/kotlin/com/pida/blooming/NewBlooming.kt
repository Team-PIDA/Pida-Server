package com.pida.blooming

sealed class NewBlooming {
    abstract val userId: Long
    abstract val status: BloomingStatus

    data class ForSpot(
        override val userId: Long,
        val flowerSpotId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming()

    data class ForEvent(
        override val userId: Long,
        val flowerEventId: Long,
        override val status: BloomingStatus,
    ) : NewBlooming()
}
