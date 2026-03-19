package com.pida.category.strategy.read

import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.category.CategoryLabel
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.support.MapCategoryBadgeBuilder
import com.pida.category.item.model.MapCategoryItem
import com.pida.flowerevent.FlowerEventFinder
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.hasBounds
import org.springframework.stereotype.Component

@Component
class EventCategoryItemReadStrategy(
    private val flowerEventFinder: FlowerEventFinder,
    private val bloomingService: BloomingService,
    private val mapCategoryBadgeFinder: MapCategoryBadgeFinder,
) : MapCategoryItemReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.EVENT

    override suspend fun read(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem> {
        val events =
            if (location.hasBounds()) {
                flowerEventFinder.readAllByCategoryIdAndLocation(categoryId, location)
            } else {
                flowerEventFinder.readAllByCategoryId(categoryId)
            }
        val recentBloomingByEventId =
            bloomingService
                .recentlyBloomingByEventIds(events.map { it.id })
                .groupBy { it.flowerEventId }
        val badgesByEventId =
            mapCategoryBadgeFinder.findAllGroupedByTarget(
                targetType = MapCategoryBadgeTargetType.FLOWER_EVENT,
                targetIds = events.map { it.id },
            )

        return events.map { event ->
            val representativeBloomingStatus =
                recentBloomingByEventId[event.id]
                    ?.groupBy { it.status }
                    ?.maxByOrNull { it.value.size }
                    ?.key ?: BloomingStatus.NOT_BLOOMED

            MapCategoryItem(
                id = event.id,
                name = event.name,
                address = event.address,
                description = null,
                thumbnailUrl = event.thumbnailUrl,
                pinPoint = event.pinPoint,
                region = event.region,
                homepageUrl = event.homepageUrl,
                startDate = event.startDate,
                endDate = event.endDate,
                bloomingStatus = representativeBloomingStatus,
                badges =
                    MapCategoryBadgeBuilder.build(
                        categoryLabel = categoryLabel,
                        region = event.region,
                        badges = badgesByEventId[event.id] ?: emptyList(),
                    ),
            )
        }
    }
}
