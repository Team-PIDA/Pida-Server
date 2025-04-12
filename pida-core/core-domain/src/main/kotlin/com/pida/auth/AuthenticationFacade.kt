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
        val existingUser = userService.getSocialUserByEmail(credentialSocial.email)

        val (socialUser, isUserNew) =
            if (existingUser != null) {
                existingUser to false
            } else {
                createNewSocialUser(credentialSocial)
            }

        val isNewUser = isUserNew || socialUser.name.isNullOrBlank()

        val token =
            authenticationService.socialLogin(
                deviceId = deviceId,
                socialUser = socialUser,
            )

        return isNewUser to token
    }

    suspend fun createNewSocialUser(credentialSocial: CredentialSocial): Pair<SocialUser, Boolean> {
        val newUser =
            userService.create(
                NewUser(
                    name = credentialSocial.name,
                    email = credentialSocial.email,
                    socialId = credentialSocial.socialId,
                    socialType = credentialSocial.socialType,
                ),
            )

        val socialUser =
            SocialUser(
                id = newUser.id,
                key = newUser.key,
                name = credentialSocial.name,
                socialId = credentialSocial.socialId,
                socialType = credentialSocial.socialType,
            )

        return socialUser to true
    }
}
