package com.pida.user

import com.pida.auth.SocialType

data class SocialUser(
    val id: Long,
    val key: String,
    val socialId: String,
    val socialType: SocialType,
)
