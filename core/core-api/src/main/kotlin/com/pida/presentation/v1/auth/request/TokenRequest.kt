package com.pida.presentation.v1.auth.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "토큰 요청 Json")
data class TokenRequest(
    @Schema(description = "토큰", example = "id_token or access_token")
    val token: String,
)
