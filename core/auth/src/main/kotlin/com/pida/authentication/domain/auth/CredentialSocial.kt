package com.pida.authentication.domain.auth

data class CredentialSocial(
    val email: String,
    val socialId: String,
    val socialType: SocialType,
)
