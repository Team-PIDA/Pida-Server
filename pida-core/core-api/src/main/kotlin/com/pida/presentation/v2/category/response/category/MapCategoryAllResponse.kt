package com.pida.presentation.v2.category.response.category

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
