package com.pida.category

import com.pida.flowerspot.FlowerSpotCafeFinder
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.hasBounds
import org.springframework.stereotype.Component

@Component
class CafeCategoryItemReadStrategy(
    private val flowerSpotCafeFinder: FlowerSpotCafeFinder,
) : MapCategoryItemReadStrategy {
    override val categoryLabel: CategoryLabel = CategoryLabel.CAFE

    override suspend fun read(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<MapCategoryItem> {
        val cafes =
            if (location.hasBounds()) {
                flowerSpotCafeFinder.readAllByLocation(location)
            } else {
                flowerSpotCafeFinder.readAll()
            }

        return cafes.map { cafe ->
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
}
