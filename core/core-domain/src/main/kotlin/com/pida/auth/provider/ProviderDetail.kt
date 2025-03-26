package com.pida.auth.provider

data class ProviderDetail(
    val id: Long,
    val userId: Long,
    val userKey: String,
    val grantedAuthorities: List<String>,
)
