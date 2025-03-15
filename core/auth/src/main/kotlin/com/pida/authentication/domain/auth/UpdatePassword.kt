package com.pida.authentication.domain.auth

data class UpdatePassword(
    val password: String,
    val newPassword: String,
)
