package com.pida.category

import com.pida.flowerspot.FlowerSpotLocation
import org.springframework.stereotype.Service

@Service
class MapCategoryFacade(
    private val mapCategoryService: MapCategoryService,
    categoryItemReadStrategies: List<MapCategoryItemReadStrategy>,
) {
    /**
     * 카테고리 ID로 카테고리를 조회한 뒤,
     * 해당 카테고리 라벨에 매핑된 전략(Strategy)을 통해 아이템 목록을 반환한다.
     *
     * - [MapCategoryService.readBy]로 카테고리 엔티티를 조회
     * - [strategiesByCategory]에서 라벨에 맞는 전략을 꺼냄 (없으면 예외)
     * - 전략의 [MapCategoryItemReadStrategy.read]를 호출하여 아이템 목록 생성
     */
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
