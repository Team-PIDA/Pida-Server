package com.pida.presentation.v1.place.request

import com.pida.place.District
import com.pida.support.geo.GeoJson
import com.pida.support.geo.toRegion
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "행정구역 데이터 저장 요청")
data class AddDistrictRequest(
    @field:NotBlank
    @field:Schema(description = "시도", example = "경기도", requiredMode = Schema.RequiredMode.REQUIRED)
    val sido: String,
    @field:NotBlank
    @field:Schema(description = "시군구", example = "용인시", requiredMode = Schema.RequiredMode.REQUIRED)
    val sigungu: String,
    @field:Schema(description = "읍면동구", example = "처인구", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val eupmyeondonggu: String?,
    @field:Schema(description = "읍면리동", example = "남사면", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val eupmyeonridong: String?,
    @field:Schema(description = "리", example = "완장리", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val ri: String?,
    @field:NotNull
    @field:Schema(description = "경도", example = "127.1746", requiredMode = Schema.RequiredMode.REQUIRED)
    val x: Double,
    @field:NotNull
    @field:Schema(description = "위도", example = "37.1678", requiredMode = Schema.RequiredMode.REQUIRED)
    val y: Double,
) {
    fun toDistrict(): District =
        District(
            id = 0L,
            sido = sido.toRegion(),
            sigungu = sigungu,
            eupmyeondonggu = eupmyeondonggu,
            eupmyeonridong = eupmyeonridong,
            ri = ri,
            pinPoint = GeoJson.Point(listOf(x, y)),
        )
}
