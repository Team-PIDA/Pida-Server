package com.pida.category

import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventFinder
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EventCategoryItemReadStrategyTest {
    @Test
    fun `위치 정보가 없으면 전체 이벤트 조회를 사용한다`(): Unit =
        runBlocking {
            val flowerEventFinder = mockk<FlowerEventFinder>()
            val strategy = EventCategoryItemReadStrategy(flowerEventFinder)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
            val event =
                FlowerEvent(
                    id = 10L,
                    name = "여의도 봄꽃축제",
                    address = "서울특별시 영등포구 여의서로 330",
                    pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                    region = Region.SEOUL,
                    homepageUrl = "https://example.com/festival",
                    startDate = LocalDate.of(2026, 3, 20),
                    endDate = LocalDate.of(2026, 3, 30),
                    categoryId = 1L,
                    deletedAt = null,
                )

            coEvery { flowerEventFinder.readAllByCategoryId(1L) } returns listOf(event)

            val result = strategy.read(1L, location)

            result shouldHaveSize 1
            result.first().id shouldBe 10L
            result.first().homepageUrl shouldBe "https://example.com/festival"
        }

    @Test
    fun `위치 정보가 있으면 위치 기반 이벤트 조회를 사용한다`(): Unit =
        runBlocking {
            val flowerEventFinder = mockk<FlowerEventFinder>()
            val strategy = EventCategoryItemReadStrategy(flowerEventFinder)
            val location = FlowerSpotLocation(swLat = 37.4, swLng = 126.8, neLat = 37.6, neLng = 127.1)
            val event =
                FlowerEvent(
                    id = 11L,
                    name = "석촌호수 벚꽃축제",
                    address = "서울특별시 송파구 잠실동",
                    pinPoint = GeoJson.Point(listOf(127.1040, 37.5070)),
                    region = Region.SEOUL,
                    homepageUrl = "https://example.com/lake-festival",
                    startDate = LocalDate.of(2026, 4, 1),
                    endDate = LocalDate.of(2026, 4, 10),
                    categoryId = 1L,
                    deletedAt = null,
                )

            coEvery { flowerEventFinder.readAllByCategoryIdAndLocation(1L, location) } returns listOf(event)

            val result = strategy.read(1L, location)

            result shouldHaveSize 1
            result.first().id shouldBe 11L
            result.first().startDate shouldBe LocalDate.of(2026, 4, 1)
        }
}
