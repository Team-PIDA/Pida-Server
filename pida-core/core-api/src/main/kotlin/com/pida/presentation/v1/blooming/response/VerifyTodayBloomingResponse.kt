package com.pida.presentation.v1.blooming.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "개화 상태 검증 응답")
data class VerifyTodayBloomingResponse(
    @Schema(description = "오늘의 개화 상태 존재 여부 (true: 존재하므로 입력 불가능, false: 입력 가능)", example = "true")
    val isBlooming: Boolean,
) {
    companion object {
        fun of(isBlooming: Boolean): VerifyTodayBloomingResponse = VerifyTodayBloomingResponse(isBlooming)
    }
}
