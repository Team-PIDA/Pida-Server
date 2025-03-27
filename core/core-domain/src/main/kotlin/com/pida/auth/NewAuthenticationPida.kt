package com.pida.auth

data class NewAuthenticationPida(
    val loginId: String,
    val password: String,
    val grantedAuthority: GrantedAuthority,
)
