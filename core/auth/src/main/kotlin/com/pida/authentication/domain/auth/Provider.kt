package com.pida.authentication.domain.auth

data class Provider(
    val id: Long,
    val userId: Long,
    val userKey: String,
)
