package com.pida.category

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.strategy.read.FlowerSpotCategoryItemReadStrategy
import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.FlowerSpotService
import com.pida.flowerspot.FlowerSpotType
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FlowerSpotCategoryItemReadStrategyTest {
    @Test
    fun `산책길 카테고리는 flower spot의 LineString과 개화 요약을 응답한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotService = mockk<FlowerSpotService>()
            val bloomingService = mockk<BloomingService>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = FlowerSpotCategoryItemReadStrategy(mapCategoryService, flowerSpotService, bloomingService, mapCategoryBadgeFinder)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
            val category =
                MapCategory(
                    id = 3L,
                    title = "산책길",
                    categoryLabel = CategoryLabel.FLOWER_SPOT,
                    description = "산책길 카테고리",
                    deletedAt = null,
                )
            val flowerSpot =
                FlowerSpot(
                    id = 30L,
                    address = "서울특별시 강남구 수서동",
                    streetName = "밤고개1길",
                    district = "수서동",
                    description = "벚꽃이 예쁜 길",
                    geom =
                        GeoJson.LineString(
                            listOf(
                                listOf(127.10079, 37.48809),
                                listOf(127.10116, 37.48825),
                            ),
                        ),
                    pinPoint = GeoJson.Point(listOf(127.10317, 37.48881)),
                    region = Region.SEOUL,
                    kind = FlowerKind.BLOSSOM,
                    type = FlowerSpotType.WALKING_TRAIL,
                    deletedAt = null,
                )

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.FLOWER_SPOT) } returns listOf(category)
            coEvery { flowerSpotService.readAllFlowerSpot(region = null, location = location) } returns listOf(flowerSpot)
            every { bloomingService.recentlyBloomingBySpotIds(listOf(30L)) } returns
                listOf(
                    Blooming(
                        id = 1L,
                        status = BloomingStatus.BLOOMED,
                        userId = 1L,
                        flowerSpotId = 30L,
                        flowerEventId = null,
                        createdAt = LocalDateTime.of(2026, 3, 19, 10, 0),
                    ),
                )
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT, listOf(30L))
            } returns
                mapOf(
                    30L to listOf(MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "10분 코스")),
                )

            val result = strategy.read(3L, null, location)

            result shouldHaveSize 1
            result.first().name shouldBe "밤고개1길"
            result.first().geom shouldBe flowerSpot.geom
            result.first().recentlyVisitedCount shouldBe 1L
            result.first().bloomingStatus shouldBe BloomingStatus.BLOOMED
            result.first().badges.map { it.type to it.label } shouldBe
                listOf(
                    MapCategoryBadgeType.SPACE_TYPE to "10분 코스",
                )
        }

    @Test
    fun `지역 필터가 있으면 해당 지역으로 산책길 조회를 위임한다`(): Unit =
        runBlocking {
            val mapCategoryService = mockk<MapCategoryService>()
            val flowerSpotService = mockk<FlowerSpotService>()
            val bloomingService = mockk<BloomingService>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = FlowerSpotCategoryItemReadStrategy(mapCategoryService, flowerSpotService, bloomingService, mapCategoryBadgeFinder)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
            val category =
                MapCategory(
                    id = 3L,
                    title = "산책길",
                    categoryLabel = CategoryLabel.FLOWER_SPOT,
                    description = "산책길 카테고리",
                    deletedAt = null,
                )

            coEvery { mapCategoryService.findAllByCategoryLabel(CategoryLabel.FLOWER_SPOT) } returns listOf(category)
            coEvery { flowerSpotService.readAllFlowerSpot(region = Region.SEOUL, location = location) } returns emptyList()
            every { bloomingService.recentlyBloomingBySpotIds(emptyList()) } returns emptyList()
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT, emptyList())
            } returns emptyMap()

            val result = strategy.read(3L, Region.SEOUL, location)

            result shouldHaveSize 0
        }
}
