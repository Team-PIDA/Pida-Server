package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.token.RefreshToken

data class RefreshTokenRequest(
    val refreshToken: String,
) {
    fun toRefreshToken(): RefreshToken =
        RefreshToken(
            token = refreshToken,
        )
}
