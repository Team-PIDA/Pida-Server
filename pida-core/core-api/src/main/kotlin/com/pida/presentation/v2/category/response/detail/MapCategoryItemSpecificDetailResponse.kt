package com.pida.presentation.v2.category.response.detail

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리별 상세 payload")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MapCategoryItemSpecificDetailResponse(
    @field:Schema(
        description = "EVENT 상세 정보",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val event: EventCategoryItemDetailPayloadResponse? = null,
    @field:Schema(
        description = "CAFE 상세 정보",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val cafe: CafeCategoryItemDetailPayloadResponse? = null,
    @field:Schema(
        description = "FLOWER_SPOT 상세 정보",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val flowerSpot: FlowerSpotCategoryItemDetailPayloadResponse? = null,
)
