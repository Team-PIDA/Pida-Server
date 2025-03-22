package com.pida.presentation.v1.auth.response

import com.pida.authentication.domain.token.Token

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun toResponse(token: Token): TokenResponse =
            TokenResponse(
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
            )
    }
}
