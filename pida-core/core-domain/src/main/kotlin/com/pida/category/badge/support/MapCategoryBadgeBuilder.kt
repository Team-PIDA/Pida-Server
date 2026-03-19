package com.pida.category.badge.support

import com.pida.category.CategoryLabel
import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeType
import com.pida.support.geo.Region

object MapCategoryBadgeBuilder {
    fun build(
        categoryLabel: CategoryLabel,
        region: Region,
        badges: List<MapCategoryBadge>,
    ): List<MapCategoryBadge> =
        when (categoryLabel) {
            CategoryLabel.EVENT ->
                buildList {
                    if (badges.none { it.type == MapCategoryBadgeType.REGION }) {
                        add(MapCategoryBadge(MapCategoryBadgeType.REGION, region.toKoreanName()))
                    }
                    addAll(badges)
                }

            CategoryLabel.CAFE ->
                buildList {
                    if (badges.none { it.type == MapCategoryBadgeType.SPACE_TYPE }) {
                        add(MapCategoryBadge(MapCategoryBadgeType.SPACE_TYPE, "카페"))
                    }
                    addAll(badges)
                }

            CategoryLabel.FLOWER_SPOT -> badges
        }
}
