package com.pida.auth

data class NewAuthenticationSocial(
    val loginId: String,
    val socialId: String,
    val socialType: SocialType,
    val grantedAuthority: GrantedAuthority,
)
