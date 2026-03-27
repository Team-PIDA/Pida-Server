package com.pida.presentation.v2.category.response.detail

import com.pida.blooming.BloomingStatus
import com.pida.presentation.v1.blooming.response.BloomingDetailsResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotImageResponse
import com.pida.presentation.v2.category.response.badge.MapCategoryBadgeResponse
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리 공통 상세 응답")
data class MapCategoryItemCommonDetailResponse(
    @field:Schema(description = "데이터 ID", example = "1")
    val id: Long,
    @field:Schema(description = "이름", example = "여의도 봄꽃축제")
    val name: String,
    @field:Schema(description = "주소", example = "서울특별시 영등포구 여의서로 330")
    val address: String?,
    @field:Schema(description = "설명", example = "벚꽃 명소 인근 카페")
    val description: String?,
    @field:Schema(
        description = "핀 포인트 정보 (GeoJson)",
        example = """
        {
          "type": "Point",
          "coordinates": [126.9340, 37.5284]
        }
    """,
    )
    val pinPoint: GeoJson,
    @field:Schema(description = "지역", example = "SEOUL")
    val region: Region,
    @field:ArraySchema(
        schema = Schema(implementation = FlowerSpotImageResponse::class),
        arraySchema = Schema(description = "상세 공통 이미지 목록"),
    )
    val imageUrls: List<FlowerSpotImageResponse>,
    @field:Schema(
        description = "대표 개화 상태",
        example = "BLOOMED",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val bloomingStatus: BloomingStatus?,
    @field:ArraySchema(
        schema = Schema(implementation = MapCategoryBadgeResponse::class),
        arraySchema = Schema(description = "카드에 노출할 배지 목록"),
    )
    val badges: List<MapCategoryBadgeResponse>,
    @field:Schema(description = "개화 상태 상세 정보", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val bloomingDetails: BloomingDetailsResponse? = null,
)
