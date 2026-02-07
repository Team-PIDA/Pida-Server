package com.pida.presentation.v1.blooming.request

import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "개화 상태 추가 요청 Request Body")
data class AddBloomingRequest(
    @Schema(description = "장소 ID", example = "1")
    val flowerSpotId: Long,
    @Schema(description = "개화 상태", example = "BLOOMED")
    val status: BloomingStatus,
) {
    fun toNewBlooming(userId: Long): NewBlooming.ForSpot =
        NewBlooming.ForSpot(
            userId = userId,
            flowerSpotId = flowerSpotId,
            status = status,
        )
}
