package com.pida.presentation.v1.auth.request

import com.pida.auth.AuthorityType
import com.pida.auth.GrantedAuthority
import com.pida.auth.NewAuthenticationSocial
import com.pida.auth.SocialType
import com.pida.user.NewUser
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 회원가입 요청 Json")
data class SignUpSocialRequest(
    val email: String,
    val name: String,
) {
    fun toNewUser(
        socialId: String,
        socialType: SocialType,
    ): NewUser =
        NewUser(
            email = email,
            name = name,
            socialId = socialId,
            socialType = socialType,
        )

    fun toNewAuthenticationSocial(
        socialId: String,
        socialType: SocialType,
    ): NewAuthenticationSocial =
        NewAuthenticationSocial(
            loginId = email,
            socialId = socialId,
            socialType = socialType,
            grantedAuthority = GrantedAuthority(AuthorityType.USER),
        )
}
