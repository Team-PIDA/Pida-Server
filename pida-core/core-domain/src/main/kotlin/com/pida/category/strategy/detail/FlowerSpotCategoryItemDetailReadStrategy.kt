package com.pida.category.strategy.detail

import com.pida.blooming.BloomingFacade
import com.pida.category.CategoryLabel
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.support.MapCategoryBadgeBuilder
import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItem
import com.pida.category.item.support.representativeBloomingStatus
import com.pida.flowerspot.FlowerSpotService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class FlowerSpotCategoryItemDetailReadStrategy(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingFacade: BloomingFacade,
    private val mapCategoryBadgeFinder: MapCategoryBadgeFinder,
) : MapCategoryItemDetailReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.FLOWER_SPOT

    override suspend fun read(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail =
        coroutineScope {
            val flowerSpotDeferred = async { flowerSpotService.readOneFlowerSpot(itemId) }
            val bloomingDetailsDeferred = async { bloomingFacade.readBloomingDetails(flowerSpotId = itemId) }

            val flowerSpot = flowerSpotDeferred.await()
            val bloomingDetails = bloomingDetailsDeferred.await()
            val representativeBloomingStatus = bloomingDetails.representativeBloomingStatus()
            val badges =
                mapCategoryBadgeFinder.findAllGroupedByTarget(
                    targetType = MapCategoryBadgeTargetType.FLOWER_SPOT,
                    targetIds = listOf(flowerSpot.id),
                )[flowerSpot.id] ?: emptyList()

            MapCategoryItemDetail(
                categoryId = categoryId,
                categoryLabel = categoryLabel,
                item =
                    MapCategoryItem(
                        id = flowerSpot.id,
                        name = flowerSpot.streetName,
                        address = flowerSpot.address,
                        description = flowerSpot.description,
                        geom = flowerSpot.geom,
                        pinPoint = flowerSpot.pinPoint,
                        region = flowerSpot.region,
                        recentlyVisitedCount = bloomingDetails.totalCount,
                        bloomingStatus = representativeBloomingStatus,
                        badges =
                            MapCategoryBadgeBuilder.build(
                                categoryLabel = categoryLabel,
                                region = flowerSpot.region,
                                badges = badges,
                            ),
                    ),
                bloomingDetails = bloomingDetails,
            )
        }
}
