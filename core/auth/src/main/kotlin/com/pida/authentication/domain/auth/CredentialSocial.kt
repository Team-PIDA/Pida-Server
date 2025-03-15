package com.pida.authentication.domain.auth

data class CredentialSocial(
    val socialId: String,
    val socialType: SocialType,
)
