package com.pida.category

import com.pida.flowerspot.FlowerSpotLocation
import org.springframework.stereotype.Service

@Service
class MapCategoryFacade(
    private val mapCategoryService: MapCategoryService,
    categoryItemReadStrategies: List<MapCategoryItemReadStrategy>,
) {
    private val strategiesByCategory =
        categoryItemReadStrategies.associateBy(MapCategoryItemReadStrategy::categoryLabel)

    init {
        require(categoryItemReadStrategies.size == strategiesByCategory.size) {
            "Map category item strategy must be unique by category label."
        }
    }

    suspend fun readAllByCategoryId(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): MapCategoryItems {
        val category = mapCategoryService.readBy(categoryId)
        val strategy =
            requireNotNull(strategiesByCategory[category.categoryLabel]) {
                "No map category item strategy for ${category.categoryLabel}"
            }

        return MapCategoryItems(
            categoryId = category.id,
            categoryLabel = category.categoryLabel,
            list = strategy.read(category.id, location),
        )
    }
}
