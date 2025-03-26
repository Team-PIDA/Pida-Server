package com.pida.presentation.v1.flowerspot

import com.pida.flowerspot.FlowerSpotFacade
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.Region
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.flowerspot.response.FlowerSpotAllResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotDetailsResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "3. Flower Spot", description = "벚꽃 장소 관련 API")
@ApiV1Controller
class FlowerSpotController(
    private val flowerSpotFacade: FlowerSpotFacade,
) {
    @Operation(summary = "벚꽃 장소 조회", description = "벚꽃 장소를 조회합니다.")
    @GetMapping("/flower-spot")
    suspend fun flowerSpotFindAll(
        @RequestParam @Parameter(name = "region", description = "지역") region: Region?,
        @RequestParam @Parameter(name = "swLat", description = "남서쪽 위도") swLat: Double?,
        @RequestParam @Parameter(name = "swLng", description = "남서쪽 경도") swLng: Double?,
        @RequestParam @Parameter(name = "neLat", description = "북동쪽 위도") neLat: Double?,
        @RequestParam @Parameter(name = "neLng", description = "북동쪽 경도") neLng: Double?,
    ): FlowerSpotAllResponse {
        val flowerSpots =
            flowerSpotFacade.findAllFlowerSpot(
                region,
                FlowerSpotLocation(
                    swLat = swLat,
                    swLng = swLng,
                    neLat = neLat,
                    neLng = neLng,
                ),
            )
        val responseDtoList = flowerSpots.map { FlowerSpotResponseDto.from(it) }
        return FlowerSpotAllResponse.of(responseDtoList)
    }

    @Operation(summary = "벚꽃 장소 상세 조회", description = "벚꽃 장소를 조회합니다.")
    @GetMapping("/flower-spot/{spotId}")
    suspend fun flowerSpotFindOne(
        @PathVariable spotId: Long,
    ): FlowerSpotDetailsResponse {
        val flowerSpot = flowerSpotFacade.findOneFlowerSpot(spotId)
        return FlowerSpotDetailsResponse.of(flowerSpot)
    }
}
