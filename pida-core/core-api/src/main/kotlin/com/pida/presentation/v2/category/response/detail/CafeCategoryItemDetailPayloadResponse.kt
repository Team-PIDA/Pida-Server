package com.pida.presentation.v2.category.response.detail

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카페 상세 정보")
data class CafeCategoryItemDetailPayloadResponse(
    @field:Schema(
        description = "대표 썸네일 이미지 URL",
        example = "https://cdn.example.com/cafe-thumbnail.jpg",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val thumbnailUrl: String?,
    @field:Schema(
        description = "카페 지도 URL",
        example = "https://place.map.kakao.com/123456",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val mapUrl: String?,
    @field:Schema(
        description = "연결된 벚꽃길 ID",
        example = "15",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val flowerSpotId: Long?,
    @field:Schema(
        description = "최근 방문 횟수",
        example = "12",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val recentlyVisitedCount: Long?,
)
