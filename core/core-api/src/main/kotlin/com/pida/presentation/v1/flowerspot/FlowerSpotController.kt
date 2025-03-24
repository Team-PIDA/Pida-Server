package com.pida.presentation.v1.flowerspot

import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.FlowerSpotService
import com.pida.flowerspot.Region
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.flowerspot.response.FlowerSpotAllResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotDetails
import com.pida.presentation.v1.flowerspot.response.FlowerSpotResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "3. Flower Spot", description = "벚꽃 장소 관련 API")
@ApiV1Controller
class FlowerSpotController(
    private val flowerSpotService: FlowerSpotService,
) {
    @Operation(summary = "벚꽃 장소 조회", description = "벚꽃 장소를 조회합니다.")
    @GetMapping("/flower-spot")
    suspend fun flowerSpotFindAll(
        @RequestParam("region") region: Region?,
        @RequestParam("swLat") swLat: Double?,
        @RequestParam("swLng") swLng: Double?,
        @RequestParam("neLat") neLat: Double?,
        @RequestParam("neLng") neLng: Double?,
    ): FlowerSpotAllResponse {
        val flowerSpots =
            flowerSpotService.findAllFlowerSpot(
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
    ): FlowerSpotDetails {
        val flowerSpot = flowerSpotService.findOneFlowerSpot(spotId)
        return FlowerSpotDetails.of(flowerSpot)
    }
}
