package com.pida.category

import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingFacade
import com.pida.blooming.BloomingStatusDetails
import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventFinder
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class EventCategoryItemDetailReadStrategyTest {
    @Test
    fun `이벤트 상세는 썸네일과 개화 상세를 함께 응답한다`(): Unit =
        runBlocking {
            val flowerEventFinder = mockk<FlowerEventFinder>()
            val bloomingFacade = mockk<BloomingFacade>()
            val strategy = EventCategoryItemDetailReadStrategy(flowerEventFinder, bloomingFacade)

            coEvery { flowerEventFinder.readBy(10L) } returns
                FlowerEvent(
                    id = 10L,
                    name = "여의도 봄꽃축제",
                    address = "서울특별시 영등포구 여의서로 330",
                    thumbnailUrl = "https://cdn.example.com/event-thumbnail.jpg",
                    pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                    region = Region.SEOUL,
                    homepageUrl = "https://example.com/festival",
                    startDate = LocalDate.of(2026, 3, 20),
                    endDate = LocalDate.of(2026, 3, 30),
                    categoryId = 1L,
                    deletedAt = null,
                )
            coEvery { bloomingFacade.readBloomingDetails(flowerEventId = 10L) } returns
                BloomingDetails(
                    totalCount = 3L,
                    nickname = "봄길러",
                    updatedAt = LocalDateTime.of(2026, 3, 19, 11, 0),
                    details =
                        mapOf(
                            "2026-03-19" to
                                mapOf(
                                    "BLOOMED" to BloomingStatusDetails(peopleCount = 2, percentage = 100),
                                ),
                            "2026-03-18" to
                                mapOf(
                                    "LITTLE" to BloomingStatusDetails(peopleCount = 1, percentage = 100),
                                ),
                        ),
                )

            val result = strategy.read(categoryId = 1L, itemId = 10L)

            result.categoryId shouldBe 1L
            result.categoryLabel shouldBe CategoryLabel.EVENT
            result.item.thumbnailUrl shouldBe "https://cdn.example.com/event-thumbnail.jpg"
            result.item.bloomingStatus shouldBe com.pida.blooming.BloomingStatus.BLOOMED
            result.bloomingDetails.totalCount shouldBe 3L
        }
}
