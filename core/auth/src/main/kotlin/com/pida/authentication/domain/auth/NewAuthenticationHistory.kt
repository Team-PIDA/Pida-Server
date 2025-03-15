package com.pida.authentication.domain.auth

import com.pida.authentication.domain.token.NewToken
import com.pida.authentication.domain.token.TokenStatus

data class NewAuthenticationHistory(
    val userId: Long,
    val userKey: String,
    val deviceId: String,
    val newToken: NewToken,
    val status: TokenStatus,
)
