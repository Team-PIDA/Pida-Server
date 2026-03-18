package com.pida.category

import com.pida.flowerevent.FlowerEventFinder
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.hasBounds
import org.springframework.stereotype.Component

@Component
class EventCategoryItemReadStrategy(
    private val flowerEventFinder: FlowerEventFinder,
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

        return events.map { event ->
            MapCategoryItem(
                id = event.id,
                name = event.name,
                address = event.address,
                description = null,
                pinPoint = event.pinPoint,
                region = event.region,
                homepageUrl = event.homepageUrl,
                startDate = event.startDate,
                endDate = event.endDate,
            )
        }
    }
}
