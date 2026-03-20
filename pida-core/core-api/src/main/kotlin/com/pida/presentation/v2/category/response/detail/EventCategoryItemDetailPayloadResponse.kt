package com.pida.presentation.v2.category.response.detail

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "이벤트 상세 정보")
data class EventCategoryItemDetailPayloadResponse(
    @field:Schema(
        description = "대표 썸네일 이미지 URL",
        example = "https://cdn.example.com/event-thumbnail.jpg",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val thumbnailUrl: String?,
    @field:Schema(
        description = "축제 홈페이지 URL",
        example = "https://example.com/festival",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val homepageUrl: String?,
    @field:Schema(
        description = "축제 시작일",
        example = "2026-03-20",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val startDate: LocalDate?,
    @field:Schema(
        description = "축제 종료일",
        example = "2026-03-30",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val endDate: LocalDate?,
)
