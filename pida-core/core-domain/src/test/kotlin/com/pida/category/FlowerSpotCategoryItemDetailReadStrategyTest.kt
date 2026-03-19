package com.pida.category

import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingFacade
import com.pida.blooming.BloomingStatus
import com.pida.blooming.BloomingStatusDetails
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.strategy.detail.FlowerSpotCategoryItemDetailReadStrategy
import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotService
import com.pida.flowerspot.FlowerSpotType
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FlowerSpotCategoryItemDetailReadStrategyTest {
    @Test
    fun `산책길 상세는 LineString과 blooming details를 응답한다`(): Unit =
        runBlocking {
            val flowerSpotService = mockk<FlowerSpotService>()
            val bloomingFacade = mockk<BloomingFacade>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = FlowerSpotCategoryItemDetailReadStrategy(flowerSpotService, bloomingFacade, mapCategoryBadgeFinder)
            val geom =
                GeoJson.LineString(
                    listOf(
                        listOf(127.10079, 37.48809),
                        listOf(127.10116, 37.48825),
                    ),
                )

            coEvery { flowerSpotService.readOneFlowerSpot(30L) } returns
                FlowerSpot(
                    id = 30L,
                    address = "서울특별시 강남구 수서동",
                    streetName = "밤고개1길",
                    district = "수서동",
                    description = "벚꽃이 예쁜 길",
                    geom = geom,
                    pinPoint = GeoJson.Point(listOf(127.10317, 37.48881)),
                    region = Region.SEOUL,
                    kind = FlowerKind.BLOSSOM,
                    type = FlowerSpotType.WALKING_TRAIL,
                    deletedAt = null,
                )
            coEvery { bloomingFacade.readBloomingDetails(flowerSpotId = 30L) } returns
                BloomingDetails(
                    totalCount = 2L,
                    nickname = "피다",
                    updatedAt = LocalDateTime.of(2026, 3, 19, 10, 0),
                    details =
                        mapOf(
                            "2026-03-19" to
                                mapOf(
                                    "BLOOMED" to BloomingStatusDetails(peopleCount = 2, percentage = 100),
                                ),
                        ),
                )
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT, listOf(30L))
            } returns
                mapOf(
                    30L to listOf(MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "10분 코스")),
                )

            val result = strategy.read(categoryId = 3L, itemId = 30L)

            result.categoryId shouldBe 3L
            result.categoryLabel shouldBe CategoryLabel.FLOWER_SPOT
            result.item.name shouldBe "밤고개1길"
            result.item.geom shouldBe geom
            result.item.recentlyVisitedCount shouldBe 2L
            result.item.bloomingStatus shouldBe BloomingStatus.BLOOMED
            result.item.badges.map { it.type to it.label } shouldBe
                listOf(
                    MapCategoryBadgeType.SPACE_TYPE to "10분 코스",
                )
        }
}
