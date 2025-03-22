package com.pida.user

import java.time.LocalDate

data class ValidateNewUser(
    val name: String,
    val birth: LocalDate,
    val gender: Gender,
    val phone: String,
)
