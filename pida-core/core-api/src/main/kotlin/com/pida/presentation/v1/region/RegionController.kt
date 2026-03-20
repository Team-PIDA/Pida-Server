package com.pida.presentation.v1.region

import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.region.response.RegionAllResponse
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "Region API", description = "지역 목록 조회 API")
@ApiV1Controller
class RegionController {
    @Operation(summary = "지역 목록 조회", description = "BFF에서 사용할 지역 목록을 조회합니다.")
    @GetMapping("/regions")
    suspend fun readAll(): RegionAllResponse = RegionAllResponse.from(Region.entries)
}
