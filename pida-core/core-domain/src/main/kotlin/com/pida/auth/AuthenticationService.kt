package com.pida.auth

import com.pida.token.RefreshToken
import com.pida.token.Token
import com.pida.user.SocialUser
import com.pida.user.User
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authenticationProcessor: AuthenticationProcessor,
    private val authenticationHistoryReader: AuthenticationHistoryReader,
) {
    fun login(
        deviceId: String?,
        user: User,
        credentialsPida: CredentialsPida,
    ): Token =
        authenticationProcessor.login(
            deviceId = deviceId,
            user = user,
            credentialsPida = credentialsPida,
        )

    fun socialLogin(
        deviceId: String,
        socialUser: SocialUser,
    ): Token =
        authenticationProcessor.login(
            deviceId = deviceId,
            socialUser = socialUser,
        )

    fun renew(refreshToken: RefreshToken): Token = authenticationProcessor.renew(refreshToken.token)

    fun logout(token: String): String = authenticationProcessor.remove(token)

    fun delete(userKey: String) {
        authenticationProcessor.withdrawal(userKey)
    }
}
