package com.pida.category

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.strategy.read.CafeCategoryItemReadStrategy
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
            val bloomingService = mockk<BloomingService>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder, bloomingService, mapCategoryBadgeFinder)
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
                    name = "벚꽃뷰 카페",
                    address = "서울특별시 송파구 석촌호수로 12",
                    description = "석촌호수 근처 카페",
                    thumbnailUrl = "https://cdn.example.com/cafe-thumbnail.jpg",
                    pinPoint = GeoJson.Point(listOf(127.1040, 37.5070)),
                    region = Region.SEOUL,
                    mapUrl = "https://place.map.kakao.com/123456",
                    deletedAt = null,
                )

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.CAFE) } returns listOf(category)
            coEvery { flowerSpotCafeFinder.readAll() } returns listOf(cafe)
            coEvery { bloomingService.recentlyBloomingByCafeIds(listOf(20L)) } returns
                listOf(
                    Blooming(
                        id = 1L,
                        status = BloomingStatus.BLOOMED,
                        userId = 1L,
                        flowerSpotId = null,
                        flowerEventId = null,
                        flowerSpotCafeId = 20L,
                        createdAt = java.time.LocalDateTime.of(2026, 3, 19, 10, 0),
                    ),
                )
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE, listOf(20L))
            } returns
                mapOf(
                    20L to listOf(MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "카공하기 좋아요")),
                )

            val result = strategy.read(2L, null, location)

            result shouldHaveSize 1
            result.first().id shouldBe 20L
            result.first().thumbnailUrl shouldBe "https://cdn.example.com/cafe-thumbnail.jpg"
            result.first().recentlyVisitedCount shouldBe 1L
            result.first().bloomingStatus shouldBe BloomingStatus.BLOOMED
            result.first().badges.map { it.type to it.label } shouldBe
                listOf(
                    MapCategoryBadgeType.SPACE_TYPE to "카공하기 좋아요",
                )
        }

    @Test
    fun `위치 정보가 있으면 위치 기반 카페 조회를 사용한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val bloomingService = mockk<BloomingService>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder, bloomingService, mapCategoryBadgeFinder)
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
                    name = "호수뷰 카페",
                    address = "서울특별시 송파구 잠실동",
                    description = "호수 근처 카페",
                    thumbnailUrl = null,
                    pinPoint = GeoJson.Point(listOf(127.1050, 37.5080)),
                    region = Region.SEOUL,
                    mapUrl = "https://place.map.kakao.com/654321",
                    deletedAt = null,
                )

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.CAFE) } returns listOf(category)
            coEvery { flowerSpotCafeFinder.readAllByLocation(location) } returns listOf(cafe)
            coEvery { bloomingService.recentlyBloomingByCafeIds(listOf(21L)) } returns emptyList()
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE, listOf(21L))
            } returns emptyMap()

            val result = strategy.read(2L, null, location)

            result shouldHaveSize 1
            result.first().id shouldBe 21L
            result.first().mapUrl shouldBe "https://place.map.kakao.com/654321"
            result.first().bloomingStatus shouldBe BloomingStatus.NOT_BLOOMED
            result.first().badges.map { it.type to it.label } shouldBe
                listOf(
                    MapCategoryBadgeType.SPACE_TYPE to "카페",
                )
        }

    @Test
    fun `활성 CAFE 카테고리가 여러 개면 예외를 던진다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val bloomingService = mockk<BloomingService>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder, bloomingService, mapCategoryBadgeFinder)
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
                kotlin
                    .runCatching {
                        strategy.read(2L, null, location)
                    }.exceptionOrNull()

            exception.shouldBeInstanceOf<IllegalStateException>()
            exception.message shouldBe "CAFE category must be uniquely mapped to one active category."
        }

    @Test
    fun `지역 필터가 있으면 해당 지역 카페만 응답한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val bloomingService = mockk<BloomingService>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = CafeCategoryItemReadStrategy(mapCategoryService, flowerSpotCafeFinder, bloomingService, mapCategoryBadgeFinder)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
            val category =
                MapCategory(
                    id = 2L,
                    title = "카페",
                    categoryLabel = CategoryLabel.CAFE,
                    description = "카페 카테고리",
                    deletedAt = null,
                )
            val seoulCafe =
                FlowerSpotCafe(
                    id = 22L,
                    name = "서울 카페",
                    address = "서울특별시 송파구 잠실동",
                    description = null,
                    thumbnailUrl = null,
                    pinPoint = GeoJson.Point(listOf(127.1050, 37.5080)),
                    region = Region.SEOUL,
                    mapUrl = null,
                    deletedAt = null,
                )
            val busanCafe =
                FlowerSpotCafe(
                    id = 23L,
                    name = "부산 카페",
                    address = "부산광역시 수영구 광안동",
                    description = null,
                    thumbnailUrl = null,
                    pinPoint = GeoJson.Point(listOf(129.1180, 35.1531)),
                    region = Region.BUSAN,
                    mapUrl = null,
                    deletedAt = null,
                )

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.CAFE) } returns listOf(category)
            coEvery { flowerSpotCafeFinder.readAll() } returns listOf(seoulCafe, busanCafe)
            coEvery { bloomingService.recentlyBloomingByCafeIds(listOf(22L)) } returns emptyList()
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE, listOf(22L))
            } returns emptyMap()

            val result = strategy.read(2L, Region.SEOUL, location)

            result shouldHaveSize 1
            result.first().id shouldBe 22L
            result.first().region shouldBe Region.SEOUL
        }
}
