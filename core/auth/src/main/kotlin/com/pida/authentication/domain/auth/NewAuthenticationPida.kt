package com.pida.authentication.domain.auth

data class NewAuthenticationPida(
    val loginId: String,
    val password: String,
    val grantedAuthority: GrantedAuthority,
)
