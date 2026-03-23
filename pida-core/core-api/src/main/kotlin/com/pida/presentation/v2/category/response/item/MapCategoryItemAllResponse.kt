package com.pida.presentation.v2.category.response.item

import com.pida.category.CategoryLabel
import com.pida.category.item.model.MapCategoryItems
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리별 데이터 목록 응답")
data class MapCategoryItemAllResponse(
    @field:Schema(description = "카테고리 ID", example = "1")
    val categoryId: Long,
    @field:Schema(description = "카테고리 라벨", example = "EVENT")
    val categoryLabel: CategoryLabel,
    @field:Schema(description = "리스트 상단 문구의 count 앞 제목", example = "2026 벚꽃 축제")
    val title: String,
    @field:Schema(description = "리스트 총 개수", example = "24")
    val count: Int,
    @field:ArraySchema(
        schema = Schema(implementation = MapCategoryItemResponse::class),
        arraySchema = Schema(description = "카테고리별 데이터 목록"),
    )
    val list: List<MapCategoryItemResponse>,
) {
    companion object {
        fun from(mapCategoryItems: MapCategoryItems) =
            MapCategoryItemAllResponse(
                categoryId = mapCategoryItems.categoryId,
                categoryLabel = mapCategoryItems.categoryLabel,
                title = mapCategoryItems.title,
                count = mapCategoryItems.count,
                list = mapCategoryItems.list.map { MapCategoryItemResponse.from(it) },
            )
    }
}
