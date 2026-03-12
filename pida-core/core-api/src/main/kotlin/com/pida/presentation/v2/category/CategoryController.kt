package com.pida.presentation.v2.category

import com.pida.category.MapCategoryService
import com.pida.presentation.v2.annotation.ApiV2Controller
import com.pida.presentation.v2.category.response.MapCategoryAllResponse
import com.pida.presentation.v2.category.response.MapCategoryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "🗂️ Category API", description = "카테고리 관련 API")
@ApiV2Controller
class CategoryController(
    private val mapCategoryService: MapCategoryService,
) {
    @Operation(summary = "카테고리 전체 조회", description = "카테고리 목록을 전체 조회합니다.")
    @GetMapping("/categories")
    suspend fun categoryFindAll(): MapCategoryAllResponse {
        val categories = mapCategoryService.findAll()
        return MapCategoryAllResponse.of(categories.map { MapCategoryResponse.from(it) })
    }
}
