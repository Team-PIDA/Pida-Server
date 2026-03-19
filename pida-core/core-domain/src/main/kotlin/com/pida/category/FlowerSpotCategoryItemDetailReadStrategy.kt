package com.pida.category

import com.pida.blooming.BloomingFacade
import com.pida.flowerspot.FlowerSpotService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class FlowerSpotCategoryItemDetailReadStrategy(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingFacade: BloomingFacade,
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
                        bloomingStatus = bloomingDetails.representativeBloomingStatus(),
                    ),
                bloomingDetails = bloomingDetails,
            )
        }
}
