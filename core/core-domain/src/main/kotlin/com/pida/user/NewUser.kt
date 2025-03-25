package com.pida.user

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
)
