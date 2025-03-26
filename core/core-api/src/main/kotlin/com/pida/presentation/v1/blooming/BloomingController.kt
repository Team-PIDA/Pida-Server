package com.pida.presentation.v1.blooming

import com.pida.blooming.BloomingService
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.blooming.request.AddBloomingRequest
import com.pida.presentation.v1.blooming.response.AddBloomingResponse
import com.pida.presentation.v1.blooming.response.BloomingDetailsResponse
import com.pida.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "4. Blooming", description = "개화 상태 관련 API")
@ApiV1Controller
class BloomingController(
    private val bloomingService: BloomingService,
) {
    @Operation(summary = "개화 상태 추가", description = "개화 상태를 추가합니다.")
    @PostMapping("/blooming")
    suspend fun bloomingAdd(
        @Parameter(hidden = true, required = false) user: User,
        @RequestBody addBloomingRequest: AddBloomingRequest,
    ): AddBloomingResponse {
        bloomingService.add(addBloomingRequest.toNewBlooming(user.id))
        return AddBloomingResponse("개화 상태가 추가되었습니다.")
    }

    @Operation(summary = "개화 상태 상세 조회", description = "개화 상태 상세 조회합니다.")
    @GetMapping("/blooming/{spotId}/details")
    suspend fun bloomingDetailsFindBySpot(
        @PathVariable spotId: Long,
    ): BloomingDetailsResponse = BloomingDetailsResponse.from(bloomingService.readAllBloomingDetailsBySpotId(spotId))
}
