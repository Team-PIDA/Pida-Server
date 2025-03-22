package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.auth.CredentialSocial
import com.pida.authentication.domain.auth.SocialType

data class LoginSocialRequest(
    val socialType: SocialType,
    val socialId: String,
    val socialToken: String,
) {
    fun toCredentialSocial() =
        CredentialSocial(
            socialId = socialId,
            socialType = socialType,
        )
}
