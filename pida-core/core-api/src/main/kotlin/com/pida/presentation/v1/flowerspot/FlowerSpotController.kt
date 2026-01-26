package com.pida.presentation.v1.flowerspot

import com.pida.flowerspot.FlowerSpotFacade
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.flowerspot.response.FlowerSpotAllResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotDetailsResponse
import com.pida.presentation.v1.flowerspot.response.FlowerSpotResponseDto
import com.pida.presentation.v1.flowerspot.response.FlowerSpotSearchResponse
import com.pida.presentation.v1.flowerspot.response.PlaceSearchResponse
import com.pida.support.geo.Region
import com.pida.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "\uD83C\uDF38 Flower Spot API", description = "벚꽃 장소 관련 API")
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
        val flowerSpot = flowerSpotFacade.readFlowerSpotDetails(spotId)
        return FlowerSpotDetailsResponse.of(flowerSpot)
    }

    @Operation(summary = "랜드마크 및 벚꽃길 검색 polling", description = "검색 우선순위에 맞게 랜드마크와 벚꽃길을 검색합니다.")
    @GetMapping("/flower-spot/search")
    fun searchFlowerSpot(
        @RequestParam @Parameter(name = "query", description = "검색 키워드") query: String,
        @Parameter(hidden = true, required = false) user: User?,
    ): FlowerSpotSearchResponse {
        val searchResult = flowerSpotFacade.search(query, user)

        return FlowerSpotSearchResponse.of(
            landmarks = searchResult.landmarks.map { PlaceSearchResponse.from(it) },
            flowerSpots = searchResult.flowerSpots.map { PlaceSearchResponse.from(it) },
        )
    }

    /*
    @Operation(summary = "랜드마크 및 벚꽃길 검색 보정", description = "지도 API에 의해 보정된 검색 결과를 업데이트 합니다.")
    @GetMapping("/flower-spot/search/refresh")
    suspend fun refreshSearchFlowerSpot(
        @RequestParam @Parameter(name = "snapshotId", description = "보정용 스냅샷 ID") snapshotId: String,
    ): FlowerSpotSearchResponse {
        val (landmarks, flowerSpots) = flowerSpotFacade.refreshSearch(snapshotId)

        return FlowerSpotSearchResponse.of(
            landmarks = landmarks.map { PlaceSearchResponse.from(it) },
            flowerSpots = flowerSpots.map { FlowerSpotResponseDto.from(it) },
        )
    }

     */
}
