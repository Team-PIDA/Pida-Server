package com.pida.category

import com.pida.blooming.BloomingFacade
import com.pida.flowerevent.FlowerEventFinder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class EventCategoryItemDetailReadStrategy(
    private val flowerEventFinder: FlowerEventFinder,
    private val bloomingFacade: BloomingFacade,
) : MapCategoryItemDetailReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.EVENT

    override suspend fun read(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail =
        coroutineScope {
            val eventDeferred = async { flowerEventFinder.readBy(itemId) }
            val bloomingDetailsDeferred = async { bloomingFacade.readBloomingDetails(flowerEventId = itemId) }

            val event = eventDeferred.await()
            val bloomingDetails = bloomingDetailsDeferred.await()

            MapCategoryItemDetail(
                categoryId = categoryId,
                categoryLabel = categoryLabel,
                item =
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
                        bloomingStatus = bloomingDetails.representativeBloomingStatus(),
                    ),
                bloomingDetails = bloomingDetails,
            )
        }
}
