package com.pida.presentation.v2.category.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.category.CategoryLabel
import com.pida.category.item.model.MapCategoryItem
import com.pida.category.item.model.MapCategoryItems
import com.pida.presentation.v2.category.response.item.MapCategoryItemAllResponse
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class MapCategoryItemAllResponseTest {
    @Test
    fun `카테고리별 데이터 목록 응답에 title과 count를 포함한다`() {
        val response =
            MapCategoryItemAllResponse.from(
                MapCategoryItems(
                    categoryId = 1L,
                    categoryLabel = CategoryLabel.EVENT,
                    title = "2026 벚꽃 축제",
                    count = 1,
                    list =
                        listOf(
                            MapCategoryItem(
                                id = 21L,
                                name = "여의도 봄꽃축제",
                                address = "서울특별시 영등포구 여의서로 330",
                                description = null,
                                pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                                region = Region.SEOUL,
                            ),
                        ),
                ),
            )

        val json = jacksonObjectMapper().writeValueAsString(response)

        json shouldContain "\"title\":\"2026 벚꽃 축제\""
        json shouldContain "\"count\":1"
    }
}
