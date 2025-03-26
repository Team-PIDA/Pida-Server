package com.pida.presentation.v1.blooming.response

import com.pida.blooming.BloomingDetails
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "개화 상태 상세 조회 응답 Json")
data class BloomingDetailsResponse(
    @Schema(description = "최근 방문한 개화 상태 수", example = "10")
    val totalCount: Long,
    @Schema(description = "개화 상태 상세 정보", example = "{ \"2025-04-01\": { \"BLOOMING\": 50, \"WITHERED\": 50 } }")
    val details: Map<String, Map<String, Int>>,
) {
    companion object {
        fun from(bloomingDetails: BloomingDetails): BloomingDetailsResponse =
            BloomingDetailsResponse(
                totalCount = bloomingDetails.totalCount,
                details = bloomingDetails.details,
            )
    }
}
