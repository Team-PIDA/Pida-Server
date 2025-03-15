package com.pida.authentication.domain.token

data class Token(
    val accessToken: String,
    val refreshToken: String,
)
