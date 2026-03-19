package com.pida.category

import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.FlowerSpotService
import org.springframework.stereotype.Component

@Component
class FlowerSpotCategoryItemReadStrategy(
    private val mapCategoryService: MapCategoryService,
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
) : MapCategoryItemReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.FLOWER_SPOT

    override suspend fun read(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem> {
        validateCategoryId(categoryId)

        val flowerSpots = flowerSpotService.readAllFlowerSpot(region = null, location = location)
        val recentBloomingBySpotId =
            bloomingService
                .recentlyBloomingBySpotIds(flowerSpots.map { it.id })
                .groupBy { it.flowerSpotId }

        return flowerSpots.map { flowerSpot ->
            val recentBloomings = recentBloomingBySpotId[flowerSpot.id] ?: emptyList()

            MapCategoryItem(
                id = flowerSpot.id,
                name = flowerSpot.streetName,
                address = flowerSpot.address,
                description = flowerSpot.description,
                geom = flowerSpot.geom,
                pinPoint = flowerSpot.pinPoint,
                region = flowerSpot.region,
                recentlyVisitedCount = recentBloomings.size.toLong(),
                bloomingStatus =
                    recentBloomings
                        .groupBy { it.status }
                        .maxByOrNull { it.value.size }
                        ?.key ?: BloomingStatus.NOT_BLOOMED,
            )
        }
    }

    private suspend fun validateCategoryId(categoryId: Long) {
        check(mapCategoryService.findAllByCategoryLabel(categoryLabel).singleOrNull()?.id == categoryId) {
            "FLOWER_SPOT category must be uniquely mapped to one active category."
        }
    }
}
