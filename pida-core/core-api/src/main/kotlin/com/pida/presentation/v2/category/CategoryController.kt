package com.pida.presentation.v2.category

import com.pida.category.MapCategoryFacade
import com.pida.category.MapCategoryService
import com.pida.presentation.v2.annotation.ApiV2Controller
import com.pida.presentation.v2.category.response.MapCategoryAllResponse
import com.pida.presentation.v2.category.response.MapCategoryItemAllResponse
import com.pida.presentation.v2.category.response.MapCategoryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "🗂️ Category API", description = "카테고리 관련 API")
@ApiV2Controller
class CategoryController(
    private val mapCategoryService: MapCategoryService,
    private val mapCategoryFacade: MapCategoryFacade,
) {
    @Operation(summary = "카테고리 전체 조회", description = "카테고리 목록을 전체 조회합니다.")
    @GetMapping("/categories")
    suspend fun categoryFindAll(): MapCategoryAllResponse {
        val categories = mapCategoryService.findAll()
        return MapCategoryAllResponse.of(categories.map { MapCategoryResponse.from(it) })
    }

    @Operation(summary = "카테고리별 데이터 조회", description = "카테고리 ID에 해당하는 데이터 목록을 조회합니다.")
    @GetMapping("/categories/{categoryId}/items")
    suspend fun categoryItemFindAll(
        @PathVariable categoryId: Long,
    ): MapCategoryItemAllResponse {
        val categoryItems = mapCategoryFacade.readAllByCategoryId(categoryId)
        return MapCategoryItemAllResponse.from(categoryItems)
    }
}
