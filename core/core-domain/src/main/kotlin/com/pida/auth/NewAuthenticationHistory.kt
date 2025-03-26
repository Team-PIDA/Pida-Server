package com.pida.auth

import com.pida.token.NewToken
import com.pida.token.TokenStatus

data class NewAuthenticationHistory(
    val userId: Long,
    val userKey: String,
    val deviceId: String?,
    val newToken: NewToken,
    val status: TokenStatus,
)
