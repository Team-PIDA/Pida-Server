package com.pida.category.strategy.detail

import com.pida.blooming.BloomingFacade
import com.pida.category.CategoryLabel
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.support.MapCategoryBadgeBuilder
import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItem
import com.pida.category.item.support.representativeBloomingStatus
import com.pida.flowerevent.FlowerEventFinder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class EventCategoryItemDetailReadStrategy(
    private val flowerEventFinder: FlowerEventFinder,
    private val bloomingFacade: BloomingFacade,
    private val mapCategoryBadgeFinder: MapCategoryBadgeFinder,
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
            val representativeBloomingStatus = bloomingDetails.representativeBloomingStatus()
            val badges =
                mapCategoryBadgeFinder.findAllGroupedByTarget(
                    targetType = MapCategoryBadgeTargetType.FLOWER_EVENT,
                    targetIds = listOf(event.id),
                )[event.id] ?: emptyList()

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
                        bloomingStatus = representativeBloomingStatus,
                        badges =
                            MapCategoryBadgeBuilder.build(
                                categoryLabel = categoryLabel,
                                region = event.region,
                                badges = badges,
                            ),
                    ),
                bloomingDetails = bloomingDetails,
            )
        }
}
