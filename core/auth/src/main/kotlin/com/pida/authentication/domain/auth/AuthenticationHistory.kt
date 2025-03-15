package com.pida.authentication.domain.auth

import com.pida.authentication.domain.token.Token
import com.pida.authentication.domain.token.TokenStatus
import java.time.LocalDateTime

data class AuthenticationHistory(
    val authenticationId: Long,
    val userKey: String,
    val deviceId: String,
    val token: Token,
    val status: TokenStatus,
    val loggedInAt: LocalDateTime,
)
