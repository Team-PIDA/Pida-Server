package com.pida.category.strategy.detail

import com.pida.blooming.BloomingFacade
import com.pida.category.CategoryLabel
import com.pida.category.badge.MapCategoryBadgeFinder
import com.pida.category.badge.model.MapCategoryBadgeTargetType
import com.pida.category.badge.support.MapCategoryBadgeBuilder
import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItem
import com.pida.category.item.support.representativeBloomingStatus
import com.pida.flowerspot.FlowerSpotCafeFinder
import org.springframework.stereotype.Component

@Component
class CafeCategoryItemDetailReadStrategy(
    private val flowerSpotCafeFinder: FlowerSpotCafeFinder,
    private val bloomingFacade: BloomingFacade,
    private val mapCategoryBadgeFinder: MapCategoryBadgeFinder,
) : MapCategoryItemDetailReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.CAFE

    override suspend fun read(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail {
        val cafe = flowerSpotCafeFinder.readBy(itemId)
        val bloomingDetails = bloomingFacade.readBloomingDetails(flowerSpotCafeId = cafe.id)
        val representativeBloomingStatus = bloomingDetails.representativeBloomingStatus()
        val badges =
            mapCategoryBadgeFinder.findAllGroupedByTarget(
                targetType = MapCategoryBadgeTargetType.FLOWER_SPOT_CAFE,
                targetIds = listOf(cafe.id),
            )[cafe.id] ?: emptyList()

        return MapCategoryItemDetail(
            categoryId = categoryId,
            categoryLabel = categoryLabel,
            item =
                MapCategoryItem(
                    id = cafe.id,
                    name = cafe.name,
                    address = cafe.address,
                    description = cafe.description,
                    thumbnailUrl = cafe.thumbnailUrl,
                    pinPoint = cafe.pinPoint,
                    region = cafe.region,
                    mapUrl = cafe.mapUrl,
                    recentlyVisitedCount = bloomingDetails.totalCount,
                    bloomingStatus = representativeBloomingStatus,
                    badges =
                        MapCategoryBadgeBuilder.build(
                            categoryLabel = categoryLabel,
                            region = cafe.region,
                            badges = badges,
                        ),
                ),
            bloomingDetails = bloomingDetails,
        )
    }
}
