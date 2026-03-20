package com.pida.presentation.v2.category.response.badge

import com.pida.category.badge.model.MapCategoryBadge
import com.pida.category.badge.model.MapCategoryBadgeType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리 배지 응답")
data class MapCategoryBadgeResponse(
    @field:Schema(description = "배지 타입", example = "BLOOMING_STATUS")
    val type: MapCategoryBadgeType,
    @field:Schema(description = "배지 문구", example = "만개예요!")
    val label: String,
) {
    companion object {
        fun from(badge: MapCategoryBadge): MapCategoryBadgeResponse =
            MapCategoryBadgeResponse(
                type = badge.type,
                label = badge.label,
            )
    }
}
