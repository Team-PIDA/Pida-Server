package com.pida.category

import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingFacade
import com.pida.blooming.BloomingStatus
import com.pida.blooming.BloomingStatusDetails
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.category.strategy.detail.CafeCategoryItemDetailReadStrategy
import com.pida.flowerspot.FlowerSpotCafe
import com.pida.flowerspot.FlowerSpotCafeFinder
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CafeCategoryItemDetailReadStrategyTest {
    @Test
    fun `카페 상세는 개화 상세를 함께 응답한다`(): Unit =
        runBlocking {
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val bloomingFacade = mockk<BloomingFacade>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = CafeCategoryItemDetailReadStrategy(flowerSpotCafeFinder, bloomingFacade, mapCategoryBadgeFinder)

            coEvery { flowerSpotCafeFinder.readBy(20L) } returns
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
            coEvery { bloomingFacade.readBloomingDetails(flowerSpotCafeId = 20L) } returns
                BloomingDetails(
                    totalCount = 5L,
                    nickname = "피다",
                    updatedAt = LocalDateTime.of(2026, 3, 19, 10, 0),
                    details =
                        mapOf(
                            "2026-03-19" to
                                mapOf(
                                    "BLOOMED" to BloomingStatusDetails(peopleCount = 3, percentage = 60),
                                    "LITTLE" to BloomingStatusDetails(peopleCount = 2, percentage = 40),
                                ),
                        ),
                )
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE, listOf(20L))
            } returns
                mapOf(
                    20L to listOf(MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "카공하기 좋아요")),
                )

            val result = strategy.read(categoryId = 2L, itemId = 20L)

            result.categoryId shouldBe 2L
            result.categoryLabel shouldBe CategoryLabel.CAFE
            result.item.thumbnailUrl shouldBe "https://cdn.example.com/cafe-thumbnail.jpg"
            result.item.recentlyVisitedCount shouldBe 5L
            result.item.bloomingStatus shouldBe BloomingStatus.BLOOMED
            result.item.badges.map { it.type to it.label } shouldBe
                listOf(
                    MapCategoryBadgeType.SPACE_TYPE to "카공하기 좋아요",
                )
        }

    @Test
    fun `카페 상세는 저장 배지가 없으면 기본 카페 배지를 응답한다`(): Unit =
        runBlocking {
            val flowerSpotCafeFinder = mockk<FlowerSpotCafeFinder>()
            val bloomingFacade = mockk<BloomingFacade>()
            val mapCategoryBadgeFinder = mockk<MapCategoryBadgeFinder>()
            val strategy = CafeCategoryItemDetailReadStrategy(flowerSpotCafeFinder, bloomingFacade, mapCategoryBadgeFinder)

            coEvery { flowerSpotCafeFinder.readBy(21L) } returns
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
            coEvery { bloomingFacade.readBloomingDetails(flowerSpotCafeId = 21L) } returns
                BloomingDetails(
                    totalCount = 1L,
                    nickname = "피다",
                    updatedAt = LocalDateTime.of(2026, 3, 19, 10, 0),
                    details =
                        mapOf(
                            "2026-03-19" to
                                mapOf(
                                    "LITTLE" to BloomingStatusDetails(peopleCount = 1, percentage = 100),
                                ),
                        ),
                )
            coEvery {
                mapCategoryBadgeFinder.findAllGroupedByTarget(MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE, listOf(21L))
            } returns emptyMap()

            val result = strategy.read(categoryId = 2L, itemId = 21L)

            result.item.badges.map { it.type to it.label } shouldBe
                listOf(
                    MapCategoryBadgeType.SPACE_TYPE to "카페",
                )
        }
}
