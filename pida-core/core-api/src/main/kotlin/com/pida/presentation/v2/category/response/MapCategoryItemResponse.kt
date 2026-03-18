package com.pida.presentation.v2.category.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.pida.category.CategoryLabel
import com.pida.category.MapCategoryItem
import com.pida.category.MapCategoryItems
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "카테고리별 데이터 목록 응답")
data class MapCategoryItemAllResponse(
    @field:Schema(description = "카테고리 ID", example = "1")
    val categoryId: Long,
    @field:Schema(description = "카테고리 라벨", example = "EVENT")
    val categoryLabel: CategoryLabel,
    @field:ArraySchema(
        schema = Schema(implementation = MapCategoryItemResponse::class),
        arraySchema = Schema(description = "카테고리별 데이터 목록"),
    )
    val list: List<MapCategoryItemResponse>,
) {
    companion object {
        fun from(mapCategoryItems: MapCategoryItems) =
            MapCategoryItemAllResponse(
                categoryId = mapCategoryItems.categoryId,
                categoryLabel = mapCategoryItems.categoryLabel,
                list = mapCategoryItems.list.map { MapCategoryItemResponse.from(it) },
            )
    }
}

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
        description = "벚꽃길 ID (CAFE인 경우에만 포함)",
        example = "15",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val flowerSpotId: Long?,
) {
    companion object {
        fun from(mapCategoryItem: MapCategoryItem) =
            MapCategoryItemResponse(
                id = mapCategoryItem.id,
                name = mapCategoryItem.name,
                address = mapCategoryItem.address,
                description = mapCategoryItem.description,
                pinPoint = mapCategoryItem.pinPoint,
                region = mapCategoryItem.region,
                homepageUrl = mapCategoryItem.homepageUrl,
                mapUrl = mapCategoryItem.mapUrl,
                startDate = mapCategoryItem.startDate,
                endDate = mapCategoryItem.endDate,
                flowerSpotId = mapCategoryItem.flowerSpotId,
            )
    }
}
