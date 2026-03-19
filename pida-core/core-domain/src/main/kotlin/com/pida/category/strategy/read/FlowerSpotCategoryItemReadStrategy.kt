package com.pida.category.strategy.read

import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.category.CategoryLabel
import com.pida.category.MapCategoryService
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.support.MapCategoryBadgeBuilder
import com.pida.category.item.model.MapCategoryItem
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.FlowerSpotService
import com.pida.support.geo.Region
import org.springframework.stereotype.Component

@Component
class FlowerSpotCategoryItemReadStrategy(
    private val mapCategoryService: MapCategoryService,
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
    private val mapCategoryBadgeFinder: MapCategoryBadgeFinder,
) : MapCategoryItemReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.FLOWER_SPOT

    override suspend fun read(
        categoryId: Long,
        region: Region?,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem> {
        validateCategoryId(categoryId)

        val flowerSpots = flowerSpotService.readAllFlowerSpot(region = region, location = location)
        val recentBloomingBySpotId =
            bloomingService
                .recentlyBloomingBySpotIds(flowerSpots.map { it.id })
                .groupBy { it.flowerSpotId }
        val badgesBySpotId =
            mapCategoryBadgeFinder.findAllGroupedByTarget(
                targetType = MapCategoryBadgeTargetType.FLOWER_SPOT,
                targetIds = flowerSpots.map { it.id },
            )

        return flowerSpots.map { flowerSpot ->
            val recentBloomings = recentBloomingBySpotId[flowerSpot.id] ?: emptyList()
            val representativeBloomingStatus =
                recentBloomings
                    .groupBy { it.status }
                    .maxByOrNull { it.value.size }
                    ?.key ?: BloomingStatus.NOT_BLOOMED

            MapCategoryItem(
                id = flowerSpot.id,
                name = flowerSpot.streetName,
                address = flowerSpot.address,
                description = flowerSpot.description,
                geom = flowerSpot.geom,
                pinPoint = flowerSpot.pinPoint,
                region = flowerSpot.region,
                recentlyVisitedCount = recentBloomings.size.toLong(),
                bloomingStatus = representativeBloomingStatus,
                badges =
                    MapCategoryBadgeBuilder.build(
                        categoryLabel = categoryLabel,
                        region = flowerSpot.region,
                        badges = badgesBySpotId[flowerSpot.id] ?: emptyList(),
                    ),
            )
        }
    }

    private suspend fun validateCategoryId(categoryId: Long) {
        check(mapCategoryService.findAllByCategoryLabel(categoryLabel).singleOrNull()?.id == categoryId) {
            "FLOWER_SPOT category must be uniquely mapped to one active category."
        }
    }
}
