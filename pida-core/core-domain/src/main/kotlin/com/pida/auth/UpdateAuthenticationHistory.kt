package com.pida.auth

import com.pida.token.NewToken

data class UpdateAuthenticationHistory(
    val userKey: String,
    val deviceId: String?,
    val refreshToken: String,
    val newToken: NewToken,
)
