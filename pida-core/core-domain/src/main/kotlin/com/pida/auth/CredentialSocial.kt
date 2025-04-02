package com.pida.auth

data class CredentialSocial(
    val email: String,
    val name: String,
    val socialId: String,
    val socialType: SocialType,
)
