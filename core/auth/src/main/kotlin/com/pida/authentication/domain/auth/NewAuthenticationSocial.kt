package com.pida.authentication.domain.auth

data class NewAuthenticationSocial(
    val loginId: String,
    val socialId: String,
    val socialType: SocialType,
    val grantedAuthority: GrantedAuthority,
)
