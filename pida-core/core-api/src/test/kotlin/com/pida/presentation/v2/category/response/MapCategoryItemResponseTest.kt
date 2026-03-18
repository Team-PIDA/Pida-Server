package com.pida.presentation.v2.category.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.category.MapCategoryItem
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
                    pinPoint = GeoJson.Point(listOf(127.1040, 37.5070)),
                    region = Region.SEOUL,
                    homepageUrl = null,
                    mapUrl = "https://place.map.kakao.com/123456",
                    startDate = null,
                    endDate = null,
                    flowerSpotId = 3L,
                ),
            )

        val json = jacksonObjectMapper().writeValueAsString(response)

        json shouldContain "\"mapUrl\""
        json shouldContain "\"flowerSpotId\""
        json shouldNotContain "\"homepageUrl\""
        json shouldNotContain "\"startDate\""
        json shouldNotContain "\"endDate\""
    }
}
