package com.pida.category.strategy.read

import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.category.CategoryLabel
import com.pida.category.MapCategoryService
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.support.MapCategoryBadgeBuilder
import com.pida.category.item.model.MapCategoryItem
import com.pida.flowerspot.FlowerSpotCafeFinder
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.hasBounds
import org.springframework.stereotype.Component

@Component
class CafeCategoryItemReadStrategy(
    private val mapCategoryService: MapCategoryService,
    private val flowerSpotCafeFinder: FlowerSpotCafeFinder,
    private val bloomingService: BloomingService,
    private val mapCategoryBadgeFinder: MapCategoryBadgeFinder,
) : MapCategoryItemReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.CAFE

    override suspend fun read(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem> {
        validateCategoryId(categoryId)

        val cafes =
            if (location.hasBounds()) {
                flowerSpotCafeFinder.readAllByLocation(location)
            } else {
                flowerSpotCafeFinder.readAll()
            }
        val recentBloomingBySpotId =
            bloomingService
                .recentlyBloomingBySpotIds(cafes.map { it.flowerSpotId })
                .groupBy { it.flowerSpotId }
        val badgesByCafeId =
            mapCategoryBadgeFinder.findAllGroupedByTarget(
                targetType = MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE,
                targetIds = cafes.map { it.id },
            )

        return cafes.map { cafe ->
            val recentBloomings = recentBloomingBySpotId[cafe.flowerSpotId] ?: emptyList()
            val representativeBloomingStatus =
                recentBloomings
                    .groupBy { it.status }
                    .maxByOrNull { it.value.size }
                    ?.key ?: BloomingStatus.NOT_BLOOMED

            MapCategoryItem(
                id = cafe.id,
                name = cafe.name,
                address = cafe.address,
                description = cafe.description,
                thumbnailUrl = cafe.thumbnailUrl,
                pinPoint = cafe.pinPoint,
                region = cafe.region,
                mapUrl = cafe.mapUrl,
                flowerSpotId = cafe.flowerSpotId,
                recentlyVisitedCount = recentBloomings.size.toLong(),
                bloomingStatus = representativeBloomingStatus,
                badges =
                    MapCategoryBadgeBuilder.build(
                        categoryLabel = categoryLabel,
                        region = cafe.region,
                        badges = badgesByCafeId[cafe.id] ?: emptyList(),
                    ),
            )
        }
    }

    private suspend fun validateCategoryId(categoryId: Long) {
        check(mapCategoryService.findAllByCategoryLabel(categoryLabel).singleOrNull()?.id == categoryId) {
            "CAFE category must be uniquely mapped to one active category."
        }
    }
}
