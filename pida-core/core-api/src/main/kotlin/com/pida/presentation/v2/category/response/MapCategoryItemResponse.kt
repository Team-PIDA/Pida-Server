package com.pida.presentation.v2.category.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.pida.blooming.BloomingStatus
import com.pida.category.CategoryLabel
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItem
import com.pida.category.item.model.MapCategoryItems
import com.pida.presentation.v1.blooming.response.BloomingDetailsResponse
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
        description = "벚꽃길 ID (CAFE인 경우에만 포함)",
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

@Schema(description = "카테고리별 데이터 상세 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MapCategoryItemDetailResponse(
    @field:Schema(description = "카테고리 ID", example = "1")
    val categoryId: Long,
    @field:Schema(description = "카테고리 라벨", example = "EVENT")
    val categoryLabel: CategoryLabel,
    @field:Schema(description = "카테고리 공통 상세 정보")
    val common: MapCategoryItemCommonDetailResponse,
    @field:Schema(description = "카테고리별 상세 정보")
    val detail: MapCategoryItemSpecificDetailResponse,
) {
    companion object {
        fun from(mapCategoryItemDetail: MapCategoryItemDetail): MapCategoryItemDetailResponse {
            val item = mapCategoryItemDetail.item

            return MapCategoryItemDetailResponse(
                categoryId = mapCategoryItemDetail.categoryId,
                categoryLabel = mapCategoryItemDetail.categoryLabel,
                common =
                    MapCategoryItemCommonDetailResponse(
                        id = item.id,
                        name = item.name,
                        address = item.address,
                        description = item.description,
                        pinPoint = item.pinPoint,
                        region = item.region,
                        bloomingStatus = item.bloomingStatus,
                        badges = item.badges.map(MapCategoryBadgeResponse::from),
                        bloomingDetails = BloomingDetailsResponse.from(mapCategoryItemDetail.bloomingDetails),
                    ),
                detail =
                    when (mapCategoryItemDetail.categoryLabel) {
                        CategoryLabel.EVENT ->
                            MapCategoryItemSpecificDetailResponse(
                                event =
                                    EventCategoryItemDetailPayloadResponse(
                                        thumbnailUrl = item.thumbnailUrl,
                                        homepageUrl = item.homepageUrl,
                                        startDate = item.startDate,
                                        endDate = item.endDate,
                                    ),
                            )

                        CategoryLabel.CAFE ->
                            MapCategoryItemSpecificDetailResponse(
                                cafe =
                                    CafeCategoryItemDetailPayloadResponse(
                                        thumbnailUrl = item.thumbnailUrl,
                                        mapUrl = item.mapUrl,
                                        flowerSpotId = item.flowerSpotId,
                                        recentlyVisitedCount = item.recentlyVisitedCount,
                                    ),
                            )

                        CategoryLabel.FLOWER_SPOT ->
                            MapCategoryItemSpecificDetailResponse(
                                flowerSpot =
                                    FlowerSpotCategoryItemDetailPayloadResponse(
                                        geom = item.geom,
                                        recentlyVisitedCount = item.recentlyVisitedCount,
                                    ),
                            )
                    },
            )
        }
    }
}

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
    @field:Schema(description = "개화 상태 상세 정보")
    val bloomingDetails: BloomingDetailsResponse,
)

@Schema(description = "카테고리 배지 응답")
data class MapCategoryBadgeResponse(
    @field:Schema(description = "배지 타입", example = "BLOOMING_STATUS")
    val type: MapCategoryBadgeType,
    @field:Schema(description = "배지 문구", example = "만개예요!")
    val label: String,
) {
    companion object {
        fun from(badge: MapCategoryBadge): MapCategoryBadgeResponse =
            MapCategoryBadgeResponse(
                type = badge.type,
                label = badge.label,
            )
    }
}

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
