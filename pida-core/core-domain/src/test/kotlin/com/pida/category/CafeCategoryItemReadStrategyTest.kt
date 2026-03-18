package com.pida.category

import com.pida.flowerspot.FlowerSpotCafe
import com.pida.flowerspot.FlowerSpotCafeFinder
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CafeCategoryItemReadStrategyTest {
    @Test
    fun `위치 정보가 없으면 전체 카페 조회를 사용한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
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

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.CAFE) } returns listOf(category)
            coEvery { flowerSpotCafeFinder.readAll() } returns listOf(cafe)

            val result = strategy.read(2L, location)

            result shouldHaveSize 1
            result.first().id shouldBe 20L
            result.first().flowerSpotId shouldBe 3L
        }

    @Test
    fun `위치 정보가 있으면 위치 기반 카페 조회를 사용한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder)
            val location = FlowerSpotLocation(swLat = 37.4, swLng = 126.8, neLat = 37.6, neLng = 127.1)
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
                    id = 21L,
                    flowerSpotId = 4L,
                    name = "호수뷰 카페",
                    address = "서울특별시 송파구 잠실동",
                    description = "호수 근처 카페",
                    pinPoint = GeoJson.Point(listOf(127.1050, 37.5080)),
                    region = Region.SEOUL,
                    mapUrl = "https://place.map.kakao.com/654321",
                    deletedAt = null,
                )

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.CAFE) } returns listOf(category)
            coEvery { flowerSpotCafeFinder.readAllByLocation(location) } returns listOf(cafe)

            val result = strategy.read(2L, location)

            result shouldHaveSize 1
            result.first().id shouldBe 21L
            result.first().mapUrl shouldBe "https://place.map.kakao.com/654321"
        }

    @Test
    fun `활성 CAFE 카테고리가 여러 개면 예외를 던진다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)

            coEvery {
                mapCategoryService.findAllByCategoryLabel(CategoryLabel.CAFE)
            } returns
                listOf(
                    MapCategory(
                        id = 2L,
                        title = "카페",
                        categoryLabel = CategoryLabel.CAFE,
                        description = "첫 번째 카페 카테고리",
                        deletedAt = null,
                    ),
                    MapCategory(
                        id = 3L,
                        title = "브런치",
                        categoryLabel = CategoryLabel.CAFE,
                        description = "두 번째 카페 카테고리",
                        deletedAt = null,
                    ),
                )

            val exception =
                kotlin.runCatching {
                    strategy.read(2L, location)
                }.exceptionOrNull()

            exception.shouldBeInstanceOf<IllegalStateException>()
            exception.message shouldBe "CAFE category must be uniquely mapped to one active category."
        }
}
