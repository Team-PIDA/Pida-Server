package com.pida.category

import com.pida.category.item.support.MapCategoryItemsTitleBuilder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class MapCategoryItemsTitleBuilderTest {
    private val fixedClock =
        Clock.fixed(
            Instant.parse("2026-03-20T00:00:00Z"),
            ZoneId.of("Asia/Seoul"),
        )

    @Test
    fun `축제 타이틀은 현재 연도와 카테고리명을 붙여 생성한다`() {
        MapCategoryItemsTitleBuilder.build(
            categoryLabel = CategoryLabel.EVENT,
            count = 3,
            clock = fixedClock,
        ) shouldBe "2026 벚꽃 축제 3곳"
    }

    @Test
    fun `카페 타이틀은 추천 문구를 반환한다`() {
        MapCategoryItemsTitleBuilder.build(
            categoryLabel = CategoryLabel.CAFE,
            count = 2,
            clock = fixedClock,
        ) shouldBe "주변에 벚꽃 뷰 카페 2곳을 찾았어요"
    }

    @Test
    fun `산책길 타이틀은 추천 문구를 반환한다`() {
        MapCategoryItemsTitleBuilder.build(
            categoryLabel = CategoryLabel.FLOWER_SPOT,
            count = 4,
            clock = fixedClock,
        ) shouldBe "주변에 걷기 좋은 산책로 4곳이 있어요"
    }
}
