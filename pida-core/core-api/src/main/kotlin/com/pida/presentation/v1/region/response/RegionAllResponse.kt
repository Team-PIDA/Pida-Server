package com.pida.presentation.v1.region.response

import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "지역 목록 응답")
data class RegionAllResponse(
    @field:ArraySchema(
        schema = Schema(implementation = RegionResponse::class),
        arraySchema = Schema(description = "지역 목록"),
    )
    val list: List<RegionResponse>,
) {
    companion object {
        fun from(regions: Iterable<Region>): RegionAllResponse = RegionAllResponse(regions.map(RegionResponse::from))
    }
}
