package com.pida.presentation.v1.flowerspot

import com.pida.flowerspot.FlowerSpotService
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.flowerspot.response.FlowerSpotAllResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "1. Flower Spot", description = "벚꽃 장소 관련 API")
@ApiV1Controller
class FlowerSpotController(
    private val flowerSpotService: FlowerSpotService,
) {
    @Operation(summary = "벚꽃 장소 조회", description = "벚꽃 장소를 조회합니다.")
    @GetMapping("/flower-spot")
    fun flowerSpotFindAll(): FlowerSpotAllResponse = FlowerSpotAllResponse.of(flowerSpotService.findAllFlowerSpot())
}
