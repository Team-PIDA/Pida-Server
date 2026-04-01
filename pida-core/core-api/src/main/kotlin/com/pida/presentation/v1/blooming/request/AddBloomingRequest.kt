package com.pida.presentation.v1.blooming.request

import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "개화 상태 추가 요청 Request Body")
data class AddBloomingRequest(
    @Schema(description = "장소 ID", example = "1", nullable = true)
    val flowerSpotId: Long? = null,
    @Schema(description = "벚꽃 축제 ID", example = "1", nullable = true)
    val flowerEventId: Long? = null,
    @Schema(description = "카페 ID", example = "1", nullable = true)
    val flowerSpotCafeId: Long? = null,
    @Schema(description = "개화 상태", example = "BLOOMED")
    val status: BloomingStatus,
) {
    fun toNewBlooming(userId: Long): NewBlooming =
        when {
            flowerSpotId != null && flowerEventId == null && flowerSpotCafeId == null ->
                NewBlooming.FlowerSpot(
                    userId = userId,
                    flowerSpotId = flowerSpotId,
                    status = status,
                )

            flowerSpotId == null && flowerEventId != null && flowerSpotCafeId == null ->
                NewBlooming.FlowerEvent(
                    userId = userId,
                    flowerEventId = flowerEventId,
                    status = status,
                )

            flowerSpotId == null && flowerEventId == null && flowerSpotCafeId != null ->
                NewBlooming.FlowerSpotCafe(
                    userId = userId,
                    flowerSpotCafeId = flowerSpotCafeId,
                    status = status,
                )

            else -> throw ErrorException(ErrorType.INVALID_REQUEST)
        }
}
