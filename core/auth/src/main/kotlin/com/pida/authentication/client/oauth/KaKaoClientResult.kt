package com.pida.authentication.client.oauth

import java.time.LocalDate

data class KaKaoClientResult(
    val id: String,
    val email: String,
    val name: String,
    val nickname: String,
    val phone: String,
    val birth: LocalDate,
    val gender: String,
)
