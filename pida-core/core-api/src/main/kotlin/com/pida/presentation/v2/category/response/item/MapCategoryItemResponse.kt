package com.pida.presentation.v2.category.response.item

import com.fasterxml.jackson.annotation.JsonInclude
import com.pida.blooming.BloomingStatus
import com.pida.category.item.model.MapCategoryItem
import com.pida.presentation.v2.category.response.badge.MapCategoryBadgeResponse
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "카테고리별 데이터 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MapCategoryItemResponse(
    @field:Schema(description = "데이터 ID", example = "1")
    val id: Long,
    @field:Schema(description = "이름", example = "여의도 봄꽃축제")
    val name: String,
    @field:Schema(description = "주소", example = "서울특별시 영등포구 여의서로 330")
    val address: String?,
    @field:Schema(description = "설명", example = "벚꽃 명소 인근 카페")
    val description: String?,
    @field:Schema(
        description = "대표 썸네일 이미지 URL",
        example = "https://cdn.example.com/event-thumbnail.jpg",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val thumbnailUrl: String?,
    @field:Schema(
        description = "라인 정보 (FLOWER_SPOT인 경우에만 포함)",
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
    @field:Schema(
        description = "축제 홈페이지 URL (EVENT인 경우에만 포함)",
        example = "https://example.com/festival",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val homepageUrl: String?,
    @field:Schema(
        description = "카페 지도 URL (CAFE인 경우에만 포함)",
        example = "https://place.map.kakao.com/123456",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val mapUrl: String?,
    @field:Schema(
        description = "축제 시작일 (EVENT인 경우에만 포함)",
        example = "2026-03-20",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val startDate: LocalDate?,
    @field:Schema(
        description = "축제 종료일 (EVENT인 경우에만 포함)",
        example = "2026-03-30",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val endDate: LocalDate?,
    @field:Schema(
        description = "벚꽃길 ID (FLOWER_SPOT인 경우에만 포함)",
        example = "15",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val flowerSpotId: Long?,
    @field:Schema(
        description = "최근 방문 횟수 (CAFE, FLOWER_SPOT인 경우에만 포함)",
        example = "12",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val recentlyVisitedCount: Long?,
    @field:Schema(
        description = "개화 상태 (EVENT, CAFE, FLOWER_SPOT에서 포함될 수 있음)",
        example = "BLOOMED",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val bloomingStatus: BloomingStatus?,
    @field:ArraySchema(
        schema = Schema(implementation = MapCategoryBadgeResponse::class),
        arraySchema = Schema(description = "카드에 노출할 배지 목록"),
    )
    val badges: List<MapCategoryBadgeResponse>,
) {
    companion object {
        fun from(mapCategoryItem: MapCategoryItem) =
            MapCategoryItemResponse(
                id = mapCategoryItem.id,
                name = mapCategoryItem.name,
                address = mapCategoryItem.address,
                description = mapCategoryItem.description,
                thumbnailUrl = mapCategoryItem.thumbnailUrl,
                geom = mapCategoryItem.geom,
                pinPoint = mapCategoryItem.pinPoint,
                region = mapCategoryItem.region,
                homepageUrl = mapCategoryItem.homepageUrl,
                mapUrl = mapCategoryItem.mapUrl,
                startDate = mapCategoryItem.startDate,
                endDate = mapCategoryItem.endDate,
                flowerSpotId = mapCategoryItem.flowerSpotId,
                recentlyVisitedCount = mapCategoryItem.recentlyVisitedCount,
                bloomingStatus = mapCategoryItem.bloomingStatus,
                badges = mapCategoryItem.badges.map(MapCategoryBadgeResponse::from),
            )
    }
}
