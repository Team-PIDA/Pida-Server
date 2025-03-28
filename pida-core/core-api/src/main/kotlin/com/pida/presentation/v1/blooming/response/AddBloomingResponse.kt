package com.pida.presentation.v1.blooming.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "개화 상태 추가 응답")
data class AddBloomingResponse(
    @Schema(description = "메시지")
    val message: String,
)
