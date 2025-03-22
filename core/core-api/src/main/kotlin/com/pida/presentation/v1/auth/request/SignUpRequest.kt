package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.auth.AuthorityType
import com.pida.authentication.domain.auth.GrantedAuthority
import com.pida.authentication.domain.auth.NewAuthenticationPida
import com.pida.user.Gender
import com.pida.user.NewUser
import java.time.LocalDate

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
    val nickname: String,
    val phone: String,
    val gender: Gender,
    val birth: LocalDate,
) {
    fun toNewUser(): NewUser =
        NewUser(
            email = email,
            name = name,
            nickname = nickname,
            phone = phone,
            gender = gender,
            birth = birth,
        )

    fun toNewAuthenticationPida(): NewAuthenticationPida =
        NewAuthenticationPida(
            loginId = email,
            password = password,
            grantedAuthority = GrantedAuthority(AuthorityType.USER),
        )
}
