package com.pida.presentation.v2.category

import com.pida.category.MapCategoryFacade
import com.pida.category.MapCategoryService
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.presentation.v2.annotation.ApiV2Controller
import com.pida.presentation.v2.category.response.category.MapCategoryAllResponse
import com.pida.presentation.v2.category.response.category.MapCategoryResponse
import com.pida.presentation.v2.category.response.detail.MapCategoryItemDetailResponse
import com.pida.presentation.v2.category.response.item.MapCategoryItemAllResponse
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

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

    @Operation(summary = "카테고리별 데이터 조회", description = "카테고리 ID에 해당하는 데이터 목록을 조회합니다. 위경도와 지역이 전달되면 해당 조건으로 필터링합니다.")
    @GetMapping("/categories/{categoryId}/items")
    suspend fun categoryItemFindAll(
        @PathVariable categoryId: Long,
        @RequestParam @Parameter(name = "region", description = "지역") region: Region?,
        @RequestParam @Parameter(name = "swLat", description = "남서쪽 위도") swLat: Double?,
        @RequestParam @Parameter(name = "swLng", description = "남서쪽 경도") swLng: Double?,
        @RequestParam @Parameter(name = "neLat", description = "북동쪽 위도") neLat: Double?,
        @RequestParam @Parameter(name = "neLng", description = "북동쪽 경도") neLng: Double?,
    ): MapCategoryItemAllResponse {
        val categoryItems =
            mapCategoryFacade.readAllByCategoryId(
                categoryId = categoryId,
                region = region,
                location =
                    FlowerSpotLocation(
                        swLat = swLat,
                        swLng = swLng,
                        neLat = neLat,
                        neLng = neLng,
                    ),
            )
        return MapCategoryItemAllResponse.from(categoryItems)
    }

    @Operation(summary = "카테고리별 상세 조회", description = "카테고리 ID와 데이터 ID에 해당하는 상세 정보를 조회합니다.")
    @GetMapping("/categories/{categoryId}/items/{itemId}")
    suspend fun categoryItemFindDetail(
        @PathVariable categoryId: Long,
        @PathVariable itemId: Long,
    ): MapCategoryItemDetailResponse =
        MapCategoryItemDetailResponse.from(
            mapCategoryFacade.readDetailByCategoryId(
                categoryId = categoryId,
                itemId = itemId,
            ),
        )
}
