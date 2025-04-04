package com.pida.presentation.v1.blooming.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "개화 상태 검증 응답")
data class VerifyTodayBloomingResponse(
    @Schema(description = "오늘의 개화 상태 검증 결과", example = "true")
    val isBlooming: Boolean,
) {
    companion object {
        fun of(isBlooming: Boolean): VerifyTodayBloomingResponse = VerifyTodayBloomingResponse(isBlooming)
    }
}
