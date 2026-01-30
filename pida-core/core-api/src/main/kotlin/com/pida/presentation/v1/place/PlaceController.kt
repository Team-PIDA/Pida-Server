package com.pida.presentation.v1.place

import com.pida.place.DistrictService
import com.pida.place.PlaceFacade
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.place.request.AddDistrictRequest
import com.pida.presentation.v1.place.response.PlaceSearchResponse
import com.pida.presentation.v1.place.response.PlaceSearchResultResponse
import com.pida.user.User
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Tag(name = "📍 Place API", description = "장소 관련 API")
@ApiV1Controller
class PlaceController(
    private val districtService: DistrictService,
    private val placeFacade: PlaceFacade,
) {
    @Hidden
    @Operation(summary = "행정구역 데이터 저장", description = "행정구역 데이터를 저장합니다.")
    @PostMapping("/places")
    @ResponseStatus(HttpStatus.CREATED)
    fun placeAdd(
        @RequestBody @Valid requests: List<AddDistrictRequest>,
    ) {
        districtService.addAll(requests.map { it.toDistrict() })
    }

    @Operation(summary = "랜드마크 및 벚꽃길 검색", description = "검색 우선순위에 맞게 랜드마크와 벚꽃길을 검색합니다.")
    @GetMapping("/places/search")
    suspend fun searchPlace(
        @RequestParam @Parameter(name = "query", description = "검색 키워드") query: String,
        @Parameter(hidden = true, required = false) user: User?,
    ): PlaceSearchResultResponse {
        val searchResult = placeFacade.search(query, user)

        return PlaceSearchResultResponse.of(
            district = searchResult.districts.map { PlaceSearchResponse.from(it) },
            landmarks = searchResult.landmarks.map { PlaceSearchResponse.from(it) },
            flowerSpots = searchResult.flowerSpots.map { PlaceSearchResponse.from(it) },
        )
    }
}
