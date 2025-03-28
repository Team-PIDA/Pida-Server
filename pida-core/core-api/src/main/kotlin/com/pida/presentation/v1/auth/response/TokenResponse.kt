package com.pida.presentation.v1.auth.response

import com.pida.token.Token
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "토큰 응답 Json")
data class TokenResponse(
    @Schema(description = "임시 토큰 여부", example = "false")
    val isTemporaryToken: Boolean = false,
    @Schema(description = "accessToken", example = "accessToken")
    val accessToken: String,
    @Schema(description = "refreshToken", example = "refreshToken")
    val refreshToken: String,
) {
    companion object {
        fun toResponse(
            isTemporaryToken: Boolean,
            token: Token,
        ): TokenResponse =
            TokenResponse(
                isTemporaryToken = isTemporaryToken,
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
            )
    }
}
