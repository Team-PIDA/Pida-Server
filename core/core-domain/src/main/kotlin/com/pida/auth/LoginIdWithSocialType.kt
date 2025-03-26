package com.pida.auth

import java.time.LocalDateTime

data class LoginIdWithSocialType(
    val loginId: String,
    val socialType: SocialType,
    val createdAt: LocalDateTime,
)
