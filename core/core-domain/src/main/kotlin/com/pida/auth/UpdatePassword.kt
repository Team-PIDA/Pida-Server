package com.pida.auth

data class UpdatePassword(
    val password: String,
    val newPassword: String,
)
