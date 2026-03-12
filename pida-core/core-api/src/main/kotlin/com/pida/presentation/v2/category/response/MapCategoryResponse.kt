package com.pida.presentation.v2.category.response

import com.pida.category.CategoryLabel
import com.pida.category.MapCategory
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리 목록 응답")
data class MapCategoryAllResponse(
    @field:ArraySchema(
        schema = Schema(implementation = MapCategoryResponse::class),
        arraySchema = Schema(description = "카테고리 목록"),
    )
    val list: List<MapCategoryResponse>,
) {
    companion object {
        fun of(list: List<MapCategoryResponse>) = MapCategoryAllResponse(list)
    }
}

@Schema(description = "카테고리 응답")
data class MapCategoryResponse(
    @param:Schema(description = "카테고리 ID", example = "1")
    val id: Long,
    @param:Schema(description = "카테고리 제목", example = "벚꽃 축제")
    val title: String,
    @param:Schema(description = "카테고리 라벨", example = "EVENT")
    val categoryLabel: CategoryLabel,
    @param:Schema(description = "카테고리 설명", example = "벚꽃 관련 축제 장소")
    val description: String?,
) {
    companion object {
        fun from(mapCategory: MapCategory) =
            MapCategoryResponse(
                id = mapCategory.id,
                title = mapCategory.title,
                categoryLabel = mapCategory.categoryLabel,
                description = mapCategory.description,
            )
    }
}
