package com.pida.authentication.domain.auth

data class UpdateLoginId(
    val loginId: String,
    val newLoginId: String,
)
