package com.pida.presentation.v2.category.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingStatus
import com.pida.blooming.BloomingStatusDetails
import com.pida.category.CategoryLabel
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItem
import com.pida.flowerspot.FlowerSpotImage
import com.pida.presentation.v2.category.response.detail.MapCategoryItemDetailResponse
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MapCategoryItemDetailResponseTest {
    @Test
    fun `상세 응답은 geom과 bloomingDetails를 함께 직렬화한다`() {
        val response =
            MapCategoryItemDetailResponse.from(
                MapCategoryItemDetail(
                    categoryId = 3L,
                    categoryLabel = CategoryLabel.FLOWER_SPOT,
                    item =
                        MapCategoryItem(
                            id = 30L,
                            name = "밤고개1길",
                            address = "서울특별시 강남구 수서동",
                            description = "벚꽃이 예쁜 길",
                            geom =
                                GeoJson.LineString(
                                    listOf(
                                        listOf(127.10079, 37.48809),
                                        listOf(127.10116, 37.48825),
                                    ),
                                ),
                            pinPoint = GeoJson.Point(listOf(127.10317, 37.48881)),
                            region = Region.SEOUL,
                            imageUrls =
                                listOf(
                                    FlowerSpotImage(
                                        url = "https://cdn.example.com/flower-spot-1.jpg",
                                        createdAt = LocalDateTime.of(2026, 3, 19, 10, 0),
                                    ),
                                ),
                            recentlyVisitedCount = 2L,
                            bloomingStatus = BloomingStatus.BLOOMED,
                            badges =
                                listOf(
                                    MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "10분 코스"),
                                ),
                        ),
                    bloomingDetails =
                        BloomingDetails(
                            totalCount = 2L,
                            nickname = "피다",
                            updatedAt = null,
                            details =
                                mapOf(
                                    "2026-03-19" to
                                        mapOf(
                                            "BLOOMED" to BloomingStatusDetails(peopleCount = 2, percentage = 100),
                                        ),
                                ),
                        ),
                ),
            )

        val json = jacksonObjectMapper().findAndRegisterModules().writeValueAsString(response)

        json shouldContain "\"categoryLabel\":\"FLOWER_SPOT\""
        json shouldContain "\"common\""
        json shouldContain "\"detail\""
        json shouldContain "\"flowerSpot\""
        json shouldContain "\"geom\""
        json shouldContain "\"bloomingDetails\""
        json shouldContain "\"bloomingStatus\":\"BLOOMED\""
        json shouldContain "\"badges\""
        json shouldContain "\"imageUrls\""
        json shouldContain "\"url\":\"https://cdn.example.com/flower-spot-1.jpg\""
        json shouldContain "\"label\":\"10분 코스\""
    }
}
