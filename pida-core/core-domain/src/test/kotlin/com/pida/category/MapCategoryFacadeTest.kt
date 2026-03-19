package com.pida.category

import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItem
import com.pida.category.strategy.detail.MapCategoryItemDetailReadStrategy
import com.pida.category.strategy.read.MapCategoryItemReadStrategy
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MapCategoryFacadeTest {
    @Test
    fun `카테고리 라벨에 맞는 전략으로 목록을 조회한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val eventStrategy = mockk<MapCategoryItemReadStrategy>()
            val cafeStrategy = mockk<MapCategoryItemReadStrategy>()
            val eventDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()
            val cafeDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()

            every { eventStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { cafeStrategy.categoryLabel } returns CategoryLabel.CAFE
            every { eventDetailStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { cafeDetailStrategy.categoryLabel } returns CategoryLabel.CAFE

            val facade =
                MapCategoryFacade(
                    mapCategoryService = mapCategoryService,
                    categoryItemReadStrategies = listOf(eventStrategy, cafeStrategy),
                    categoryItemDetailReadStrategies = listOf(eventDetailStrategy, cafeDetailStrategy),
                )

            val category =
                MapCategory(
                    id = 1L,
                    title = "벚꽃 축제",
                    categoryLabel = CategoryLabel.EVENT,
                    description = "벚꽃 축제 카테고리",
                    deletedAt = null,
                )
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
            val item =
                MapCategoryItem(
                    id = 10L,
                    name = "여의도 봄꽃축제",
                    address = "서울특별시 영등포구 여의서로 330",
                    description = null,
                    thumbnailUrl = null,
                    pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                    region = Region.SEOUL,
                    homepageUrl = "https://example.com/festival",
                    mapUrl = null,
                    startDate = LocalDate.of(2026, 3, 20),
                    endDate = LocalDate.of(2026, 3, 30),
                    flowerSpotId = null,
                )

            coEvery { mapCategoryService.readBy(1L) } returns category
            coEvery { eventStrategy.read(1L, null, location) } returns listOf(item)

            val result = facade.readAllByCategoryId(1L, null, location)

            result.categoryId shouldBe 1L
            result.categoryLabel shouldBe CategoryLabel.EVENT
            result.list shouldHaveSize 1
            result.list.first() shouldBe item
        }

    @Test
    fun `전략은 카테고리별로 하나만 등록할 수 있다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val firstEventStrategy = mockk<MapCategoryItemReadStrategy>()
            val secondEventStrategy = mockk<MapCategoryItemReadStrategy>()
            val eventDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()

            every { firstEventStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { secondEventStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { eventDetailStrategy.categoryLabel } returns CategoryLabel.EVENT

            val exception =
                kotlin
                    .runCatching {
                        MapCategoryFacade(
                            mapCategoryService = mapCategoryService,
                            categoryItemReadStrategies = listOf(firstEventStrategy, secondEventStrategy),
                            categoryItemDetailReadStrategies = listOf(eventDetailStrategy),
                        )
                    }.exceptionOrNull()

            exception?.message shouldBe "Map category item strategy must be unique by category label."
        }

    @Test
    fun `카테고리 라벨에 맞는 전략으로 상세를 조회한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val eventStrategy = mockk<MapCategoryItemReadStrategy>()
            val cafeStrategy = mockk<MapCategoryItemReadStrategy>()
            val eventDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()
            val cafeDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()

            every { eventStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { cafeStrategy.categoryLabel } returns CategoryLabel.CAFE
            every { eventDetailStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { cafeDetailStrategy.categoryLabel } returns CategoryLabel.CAFE

            val facade =
                MapCategoryFacade(
                    mapCategoryService = mapCategoryService,
                    categoryItemReadStrategies = listOf(eventStrategy, cafeStrategy),
                    categoryItemDetailReadStrategies = listOf(eventDetailStrategy, cafeDetailStrategy),
                )

            val category =
                MapCategory(
                    id = 1L,
                    title = "벚꽃 축제",
                    categoryLabel = CategoryLabel.EVENT,
                    description = "벚꽃 축제 카테고리",
                    deletedAt = null,
                )
            val detail =
                MapCategoryItemDetail(
                    categoryId = 1L,
                    categoryLabel = CategoryLabel.EVENT,
                    item =
                        MapCategoryItem(
                            id = 10L,
                            name = "여의도 봄꽃축제",
                            address = "서울특별시 영등포구 여의서로 330",
                            description = null,
                            pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                            region = Region.SEOUL,
                            homepageUrl = "https://example.com/festival",
                        ),
                    bloomingDetails =
                        com.pida.blooming.BloomingDetails(
                            totalCount = 1L,
                            nickname = "피다",
                            updatedAt = null,
                            details = emptyMap(),
                        ),
                )

            coEvery { mapCategoryService.readBy(1L) } returns category
            coEvery { eventDetailStrategy.read(1L, 10L) } returns detail

            val result = facade.readDetailByCategoryId(1L, 10L)

            result shouldBe detail
        }

    @Test
    fun `상세 전략은 카테고리별로 하나만 등록할 수 있다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val eventStrategy = mockk<MapCategoryItemReadStrategy>()
            val firstEventDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()
            val secondEventDetailStrategy = mockk<MapCategoryItemDetailReadStrategy>()

            every { eventStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { firstEventDetailStrategy.categoryLabel } returns CategoryLabel.EVENT
            every { secondEventDetailStrategy.categoryLabel } returns CategoryLabel.EVENT

            val exception =
                kotlin
                    .runCatching {
                        MapCategoryFacade(
                            mapCategoryService = mapCategoryService,
                            categoryItemReadStrategies = listOf(eventStrategy),
                            categoryItemDetailReadStrategies = listOf(firstEventDetailStrategy, secondEventDetailStrategy),
                        )
                    }.exceptionOrNull()

            exception?.message shouldBe "Map category item detail strategy must be unique by category label."
        }
}
