package com.pida.user

import java.time.LocalDate

/**
 * NewUser
 *
 * @property email 이메일
 * @property name 이름
 * @property nickname 닉네임
 * @property phone 휴대폰 번호
 * @property gender 성별
 * @property birth 생일
 */
data class NewUser(
    val name: String,
    val nickname: String,
    val phone: String,
    val email: String,
    val gender: Gender,
    val birth: LocalDate,
)
