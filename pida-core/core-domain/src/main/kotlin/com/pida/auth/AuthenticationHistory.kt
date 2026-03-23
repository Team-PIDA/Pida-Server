package com.pida.auth

import com.pida.token.Token
import com.pida.token.TokenStatus
import java.time.LocalDateTime

data class AuthenticationHistory(
    val authenticationId: Long,
    val userId: Long,
    val userKey: String,
    val deviceId: String?,
    val token: Token,
    val status: TokenStatus,
    val loggedInAt: LocalDateTime,
)
