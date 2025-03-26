package com.pida.auth

data class Provider(
    val id: Long,
    val userId: Long,
    val userKey: String,
)
