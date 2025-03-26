package com.pida.presentation.v1.auth.request

import com.pida.auth.AuthorityType
import com.pida.auth.GrantedAuthority
import com.pida.auth.NewAuthenticationSocial
import com.pida.auth.SocialType
import com.pida.user.NewUser

data class SignUpSocialRequest(
    val email: String,
    val name: String,
    val nickName: String,
    val socialId: String,
    val socialToken: String,
    val socialType: SocialType,
) {
    fun toNewUser(): NewUser =
        NewUser(
            email = email,
            name = name,
            nickname = nickName,
        )

    fun toNewAuthenticationSocial(): NewAuthenticationSocial =
        NewAuthenticationSocial(
            loginId = email,
            socialId = socialId,
            socialType = socialType,
            grantedAuthority = GrantedAuthority(AuthorityType.USER),
        )
}
