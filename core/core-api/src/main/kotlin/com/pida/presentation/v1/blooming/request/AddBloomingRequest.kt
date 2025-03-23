package com.pida.presentation.v1.blooming.request

import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming

data class AddBloomingRequest(
    val flowerSpotId: Long,
    val status: BloomingStatus,
) {
    fun toNewBlooming(userId: Long): NewBlooming =
        NewBlooming(
            userId = userId,
            flowerSpotId = flowerSpotId,
            status = status,
        )
}
