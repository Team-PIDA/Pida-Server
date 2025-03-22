package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.auth.AuthorityType
import com.pida.authentication.domain.auth.GrantedAuthority
import com.pida.authentication.domain.auth.NewAuthenticationSocial
import com.pida.authentication.domain.auth.SocialType
import com.pida.user.Gender
import com.pida.user.NewUser
import java.time.LocalDate

data class SignUpSocialRequest(
    val email: String,
    val name: String,
    val nickName: String,
    val phone: String,
    val gender: Gender,
    val birth: LocalDate,
    val socialId: String,
    val socialToken: String,
    val socialType: SocialType,
) {
    fun toNewUser(): NewUser =
        NewUser(
            email = email,
            name = name,
            nickname = nickName,
            phone = phone,
            gender = gender,
            birth = birth,
        )

    fun toNewAuthenticationSocial(): NewAuthenticationSocial =
        NewAuthenticationSocial(
            loginId = email,
            socialId = socialId,
            socialType = socialType,
            grantedAuthority = GrantedAuthority(AuthorityType.USER),
        )
}
