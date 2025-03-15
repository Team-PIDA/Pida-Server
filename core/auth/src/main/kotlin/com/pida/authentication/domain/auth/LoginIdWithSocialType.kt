package com.pida.authentication.domain.auth

import java.time.LocalDateTime

data class LoginIdWithSocialType(
    val loginId: String,
    val socialType: SocialType,
    val createdAt: LocalDateTime,
)
