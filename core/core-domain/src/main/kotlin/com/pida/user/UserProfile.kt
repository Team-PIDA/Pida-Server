package com.pida.user

import java.time.LocalDate
import java.time.LocalDateTime

data class UserProfile(
    val id: Long,
    val key: String,
    val email: String,
    val name: String,
    val nickname: String,
    val phone: String,
    val gender: Gender,
    val birth: LocalDate,
    val createdAt: LocalDateTime,
)
