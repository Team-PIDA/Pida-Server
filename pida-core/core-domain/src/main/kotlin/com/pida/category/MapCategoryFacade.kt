package com.pida.category

import com.pida.category.item.detail.MapCategoryItemDetail
import com.pida.category.item.model.MapCategoryItems
import com.pida.category.item.support.MapCategoryItemsTitleBuilder
import com.pida.category.strategy.detail.MapCategoryItemDetailReadStrategy
import com.pida.category.strategy.read.MapCategoryItemReadStrategy
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.support.geo.Region
import org.springframework.stereotype.Service

@Service
class MapCategoryFacade(
    private val mapCategoryService: MapCategoryService,
    categoryItemReadStrategies: List<MapCategoryItemReadStrategy>,
    categoryItemDetailReadStrategies: List<MapCategoryItemDetailReadStrategy>,
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
    private val detailStrategiesByCategory =
        categoryItemDetailReadStrategies.associateBy(MapCategoryItemDetailReadStrategy::categoryLabel)

    init {
        require(categoryItemReadStrategies.size == strategiesByCategory.size) {
            "Map category item strategy must be unique by category label."
        }
        require(categoryItemDetailReadStrategies.size == detailStrategiesByCategory.size) {
            "Map category item detail strategy must be unique by category label."
        }
    }

    suspend fun readAllByCategoryId(
        categoryId: Long,
        region: Region?,
        location: FlowerSpotLocation,
    ): MapCategoryItems {
        val category = mapCategoryService.readBy(categoryId)
        val strategy =
            requireNotNull(strategiesByCategory[category.categoryLabel]) {
                "No map category item strategy for ${category.categoryLabel}"
            }
        val items = strategy.read(category.id, region, location)

        return MapCategoryItems(
            categoryId = category.id,
            categoryLabel = category.categoryLabel,
            title =
                MapCategoryItemsTitleBuilder.build(
                    categoryLabel = category.categoryLabel,
                    count = items.size,
                ),
            count = items.size,
            list = items,
        )
    }

    suspend fun readDetailByCategoryId(
        categoryId: Long,
        itemId: Long,
    ): MapCategoryItemDetail {
        val category = mapCategoryService.readBy(categoryId)
        val strategy =
            requireNotNull(detailStrategiesByCategory[category.categoryLabel]) {
                "No map category item detail strategy for ${category.categoryLabel}"
            }

        return strategy.read(category.id, itemId)
    }
}
