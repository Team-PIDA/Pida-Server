package com.pida.presentation.v1.auth.request

import com.pida.token.RefreshToken
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "리프레시 토큰 요청")
data class RefreshTokenRequest(
    @Schema(description = "리프레시 토큰", example = "refresh_token")
    val refreshToken: String,
) {
    fun toRefreshToken(): RefreshToken =
        RefreshToken(
            token = refreshToken,
        )
}
