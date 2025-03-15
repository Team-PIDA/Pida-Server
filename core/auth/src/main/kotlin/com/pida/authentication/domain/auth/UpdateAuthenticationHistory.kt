package com.pida.authentication.domain.auth

import com.pida.authentication.domain.token.NewToken

data class UpdateAuthenticationHistory(
    val userKey: String,
    val deviceId: String,
    val refreshToken: String,
    val newToken: NewToken,
)
