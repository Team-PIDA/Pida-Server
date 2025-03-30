package com.pida.presentation.v1.user.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "애플 토큰 만료 요청")
data class AppleRefreshTokenRequest(
    val refreshToken: String,
)
