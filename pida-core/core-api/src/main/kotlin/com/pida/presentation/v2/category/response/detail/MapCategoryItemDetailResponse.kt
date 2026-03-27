package com.pida.presentation.v2.category.response.detail

import com.fasterxml.jackson.annotation.JsonInclude
import com.pida.category.CategoryLabel
import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.presentation.v1.blooming.response.BloomingDetailsResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotImageResponse
import com.pida.presentation.v2.category.response.badge.MapCategoryBadgeResponse
import io.swagger.v3.oas.annotations.media.Schema

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
                        imageUrls = item.imageUrls.map(FlowerSpotImageResponse::from),
                        bloomingStatus = item.bloomingStatus,
                        badges = item.badges.map(MapCategoryBadgeResponse::from),
                        bloomingDetails = mapCategoryItemDetail.bloomingDetails?.let { BloomingDetailsResponse.from(it) },
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
