package com.pida.presentation.v1.place

import com.pida.place.DistrictService
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.place.request.AddDistrictRequest
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Tag(name = "📍 Place API", description = "장소 관련 API")
@ApiV1Controller
class PlaceController(
    private val districtService: DistrictService,
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
}
