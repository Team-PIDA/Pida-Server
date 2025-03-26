package com.pida.user

import com.pida.auth.SocialType

/**
 * NewUser
 *
 * @property email 이메일
 * @property name 이름
 * @property nickname 닉네임
 */
data class NewUser(
    val name: String,
    val nickname: String,
    val email: String,
    val password: String? = null,
    val socialId: String,
    val socialType: SocialType,
)
