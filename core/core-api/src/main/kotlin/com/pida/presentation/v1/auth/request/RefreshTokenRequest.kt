package com.pida.presentation.v1.auth.request

import com.pida.token.RefreshToken

data class RefreshTokenRequest(
    val refreshToken: String,
) {
    fun toRefreshToken(): RefreshToken =
        RefreshToken(
            token = refreshToken,
        )
}
