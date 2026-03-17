package com.pida.category

import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventFinder
import com.pida.flowerspot.FlowerSpotCafe
import com.pida.flowerspot.FlowerSpotCafeFinder
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MapCategoryFacadeTest {
    @Test
    fun `EVENT 카테고리는 축제 목록으로 매핑한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerEventFinder = mockk<FlowerEventFinder>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val service = MapCategoryFacade(mapCategoryService, flowerEventFinder, flowerSpotCafeFinder)

            val category =
                MapCategory(
                    id = 1L,
                    title = "벚꽃 축제",
                    categoryLabel = CategoryLabel.EVENT,
                    description = "벚꽃 축제 카테고리",
                    deletedAt = null,
                )
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

            coEvery { mapCategoryService.readBy(1L) } returns category
            coEvery { flowerEventFinder.readAllByCategoryId(1L) } returns listOf(event)

            val result = service.readAllByCategoryId(1L)

            result.categoryId shouldBe 1L
            result.categoryLabel shouldBe CategoryLabel.EVENT
            result.list shouldHaveSize 1
            result.list.first() shouldBe
                MapCategoryItem(
                    id = 10L,
                    name = "여의도 봄꽃축제",
                    address = "서울특별시 영등포구 여의서로 330",
                    description = null,
                    pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                    region = Region.SEOUL,
                    homepageUrl = "https://example.com/festival",
                    mapUrl = null,
                    startDate = LocalDate.of(2026, 3, 20),
                    endDate = LocalDate.of(2026, 3, 30),
                    flowerSpotId = null,
                )
        }

    @Test
    fun `CAFE 카테고리는 카페 목록으로 매핑한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerEventFinder = mockk<FlowerEventFinder>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val service = MapCategoryFacade(mapCategoryService, flowerEventFinder, flowerSpotCafeFinder)

            val category =
                MapCategory(
                    id = 2L,
                    title = "카페",
                    categoryLabel = CategoryLabel.CAFE,
                    description = "카페 카테고리",
                    deletedAt = null,
                )
            val cafe =
                FlowerSpotCafe(
                    id = 20L,
                    flowerSpotId = 3L,
                    name = "벚꽃뷰 카페",
                    address = "서울특별시 송파구 석촌호수로 12",
                    description = "석촌호수 근처 카페",
                    pinPoint = GeoJson.Point(listOf(127.1040, 37.5070)),
                    region = Region.SEOUL,
                    mapUrl = "https://place.map.kakao.com/123456",
                    deletedAt = null,
                )

            coEvery { mapCategoryService.readBy(2L) } returns category
            coEvery { flowerSpotCafeFinder.readAll() } returns listOf(cafe)

            val result = service.readAllByCategoryId(2L)

            result.categoryId shouldBe 2L
            result.categoryLabel shouldBe CategoryLabel.CAFE
            result.list shouldHaveSize 1
            result.list.first() shouldBe
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
                )
        }
}
