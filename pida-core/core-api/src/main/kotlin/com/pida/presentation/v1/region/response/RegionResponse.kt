package com.pida.presentation.v1.region.response

import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "지역 응답")
data class RegionResponse(
    @field:Schema(description = "지역 코드", example = "SEOUL")
    val code: Region,
    @field:Schema(description = "지역 이름", example = "서울")
    val name: String,
) {
    companion object {
        fun from(region: Region): RegionResponse =
            RegionResponse(
                code = region,
                name = region.toKoreanName(),
            )
    }
}
