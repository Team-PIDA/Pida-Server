package com.pida.auth

import com.pida.token.Token
import com.pida.user.NewUser
import com.pida.user.SocialUser
import com.pida.user.UserService
import org.springframework.stereotype.Service

@Service
class AuthenticationFacade(
    private val userService: UserService,
    private val authenticationService: AuthenticationService,
) {
    suspend fun socialLogin(
        deviceId: String,
        credentialSocial: CredentialSocial,
    ): Pair<Boolean, Token> {
        val (socialUser, isNewUser) =
            userService
                .getSocialUserByEmail(credentialSocial.email)
                ?.let { it to false }
                ?: userService
                    .create(
                        NewUser(
                            name = "",
                            email = credentialSocial.email,
                            socialId = credentialSocial.socialId,
                            socialType = credentialSocial.socialType,
                        ),
                    ).let {
                        SocialUser(
                            id = it.id,
                            key = it.key,
                            socialId = credentialSocial.socialId,
                            socialType = credentialSocial.socialType,
                        ) to true
                    }

        val token =
            authenticationService.socialLogin(
                deviceId = deviceId,
                socialUser = socialUser,
            )

        return isNewUser to token
    }
}
