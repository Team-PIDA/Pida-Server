package com.pida.presentation.v2.category.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.blooming.BloomingStatus
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.item.model.MapCategoryItem
import com.pida.presentation.v2.category.response.item.MapCategoryItemResponse
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class MapCategoryItemResponseTest {
    @Test
    fun `null 필드는 직렬화에서 제외한다`() {
        val response =
            MapCategoryItemResponse.from(
                MapCategoryItem(
                    id = 20L,
                    name = "벚꽃뷰 카페",
                    address = "서울특별시 송파구 석촌호수로 12",
                    description = "석촌호수 근처 카페",
                    thumbnailUrl = "https://cdn.example.com/cafe-thumbnail.jpg",
                    pinPoint = GeoJson.Point(listOf(127.1040, 37.5070)),
                    region = Region.SEOUL,
                    homepageUrl = null,
                    mapUrl = "https://place.map.kakao.com/123456",
                    startDate = null,
                    endDate = null,
                    flowerSpotId = 3L,
                    recentlyVisitedCount = 7L,
                    badges =
                        listOf(
                            MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "카공하기 좋아요"),
                        ),
                ),
            )

        val json = jacksonObjectMapper().writeValueAsString(response)

        json shouldContain "\"mapUrl\""
        json shouldContain "\"flowerSpotId\""
        json shouldContain "\"thumbnailUrl\""
        json shouldContain "\"recentlyVisitedCount\":7"
        json shouldContain "\"badges\""
        json shouldContain "\"type\":\"SPACE_TYPE\""
        json shouldContain "\"label\":\"카공하기 좋아요\""
        json shouldNotContain "\"homepageUrl\""
        json shouldNotContain "\"geom\""
        json shouldNotContain "\"startDate\""
        json shouldNotContain "\"endDate\""
        json shouldNotContain "\"bloomingStatus\""
    }

    @Test
    fun `이벤트 개화 상태가 있으면 응답에 포함한다`() {
        val response =
            MapCategoryItemResponse.from(
                MapCategoryItem(
                    id = 21L,
                    name = "여의도 봄꽃축제",
                    address = "서울특별시 영등포구 여의서로 330",
                    description = null,
                    thumbnailUrl = "https://cdn.example.com/event-thumbnail.jpg",
                    pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                    region = Region.SEOUL,
                    homepageUrl = "https://example.com/festival",
                    bloomingStatus = BloomingStatus.BLOOMED,
                    badges =
                        listOf(
                            MapCategoryBadge(MapCategoryBadgeType.REGION, "서울"),
                        ),
                ),
            )

        val json = jacksonObjectMapper().writeValueAsString(response)

        json shouldContain "\"bloomingStatus\":\"BLOOMED\""
        json shouldContain "\"thumbnailUrl\""
        json shouldContain "\"type\":\"REGION\""
        json shouldContain "\"label\":\"서울\""
    }
}
