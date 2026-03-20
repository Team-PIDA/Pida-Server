package com.pida.presentation.v2.category.response.detail

import com.pida.support.geo.GeoJson
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "산책길 상세 정보")
data class FlowerSpotCategoryItemDetailPayloadResponse(
    @field:Schema(
        description = "라인 정보 (GeoJson)",
        example = """
        {
          "type": "LineString",
          "coordinates": [
            [127.10079, 37.48809],
            [127.10116, 37.48825],
            [127.10221, 37.48852]
          ]
        }
    """,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val geom: GeoJson?,
    @field:Schema(
        description = "최근 방문 횟수",
        example = "12",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val recentlyVisitedCount: Long?,
)
