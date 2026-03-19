package com.pida.category

import com.pida.blooming.BloomingFacade
import com.pida.flowerspot.FlowerSpotCafeFinder
import org.springframework.stereotype.Component

@Component
class CafeCategoryItemDetailReadStrategy(
    private val flowerSpotCafeFinder: FlowerSpotCafeFinder,
    private val bloomingFacade: BloomingFacade,
) : MapCategoryItemDetailReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.CAFE

    override suspend fun read(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail {
        val cafe = flowerSpotCafeFinder.readBy(itemId)
        val bloomingDetails = bloomingFacade.readBloomingDetails(flowerSpotId = cafe.flowerSpotId)

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
                    flowerSpotId = cafe.flowerSpotId,
                    recentlyVisitedCount = bloomingDetails.totalCount,
                    bloomingStatus = bloomingDetails.representativeBloomingStatus(),
                ),
            bloomingDetails = bloomingDetails,
        )
    }
}
