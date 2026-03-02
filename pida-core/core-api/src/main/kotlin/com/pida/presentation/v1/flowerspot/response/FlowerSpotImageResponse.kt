package com.pida.presentation.v1.flowerspot.response

import com.pida.flowerspot.FlowerSpotImage
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "벚꽃길 이미지 응답")
data class FlowerSpotImageResponse(
    @field:Schema(description = "이미지 URL", example = "https://example.com/image1.jpg")
    val url: String,
    @field:Schema(description = "등록 일자", example = "2025-04-01T12:00:00")
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(image: FlowerSpotImage) =
            FlowerSpotImageResponse(
                url = image.url,
                createdAt = image.createdAt,
            )
    }
}
