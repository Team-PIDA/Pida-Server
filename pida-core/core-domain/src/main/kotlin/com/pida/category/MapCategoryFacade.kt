package com.pida.category

import com.pida.flowerevent.FlowerEventFinder
import com.pida.flowerspot.FlowerSpotCafeFinder
import org.springframework.stereotype.Service

@Service
class MapCategoryFacade(
    private val mapCategoryService: MapCategoryService,
    private val flowerEventFinder: FlowerEventFinder,
    private val flowerSpotCafeFinder: FlowerSpotCafeFinder,
) {
    suspend fun readAllByCategoryId(categoryId: Long): MapCategoryItems {
        val category = mapCategoryService.readBy(categoryId)
        val items =
            when (category.categoryLabel) {
                CategoryLabel.EVENT ->
                    flowerEventFinder.readAllByCategoryId(category.id).map { event ->
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

                CategoryLabel.CAFE ->
                    flowerSpotCafeFinder.readAll().map { cafe ->
                        MapCategoryItem(
                            id = cafe.id,
                            name = cafe.name,
                            address = cafe.address,
                            description = cafe.description,
                            pinPoint = cafe.pinPoint,
                            region = cafe.region,
                            mapUrl = cafe.mapUrl,
                            flowerSpotId = cafe.flowerSpotId,
                        )
                    }
            }

        return MapCategoryItems(
            categoryId = category.id,
            categoryLabel = category.categoryLabel,
            list = items,
        )
    }
}
