package com.pida.authentication.domain.auth.service

import com.pida.authentication.domain.auth.AuthenticationPida
import com.pida.authentication.domain.auth.AuthenticationSns
import com.pida.authentication.domain.auth.CredentialSocial
import com.pida.authentication.domain.auth.CredentialsPida
import com.pida.authentication.domain.auth.LoginIdWithSocialType
import com.pida.authentication.domain.auth.NewAuthenticationSocial
import com.pida.authentication.domain.auth.UpdateLoginId
import com.pida.authentication.domain.auth.UpdatePassword
import com.pida.authentication.domain.auth.component.AuthenticationProcessor
import com.pida.authentication.domain.auth.component.AuthenticationReader
import com.pida.authentication.domain.auth.component.AuthenticationUpdater
import com.pida.authentication.domain.auth.component.AuthenticationValidator
import com.pida.authentication.domain.token.RefreshToken
import com.pida.authentication.domain.token.Token
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authenticationProcessor: AuthenticationProcessor,
    private val authenticationReader: AuthenticationReader,
    private val authenticationUpdater: AuthenticationUpdater,
    private val authenticationValidator: AuthenticationValidator,
) {
    fun signUp(
        userId: Long,
        userKey: String,
        newAuthenticationSocial: NewAuthenticationSocial,
    ): AuthenticationSns {
        authenticationValidator.verifySns(newAuthenticationSocial)
        return authenticationProcessor.createAuthentication(
            userId = userId,
            userKey = userKey,
            newAuthenticationSocial = newAuthenticationSocial,
        )
    }

    fun login(
        deviceId: String,
        credentialsPida: CredentialsPida,
    ): Token =
        authenticationProcessor.login(
            deviceId = deviceId,
            credentialsPida = credentialsPida,
        )

    fun socialLogin(
        deviceId: String,
        credentialSocial: CredentialSocial,
    ): Token =
        authenticationProcessor.login(
            deviceId = deviceId,
            credentialSocial = credentialSocial,
        )

    fun renew(refreshToken: RefreshToken): Token = authenticationProcessor.renew(refreshToken.token)

    fun logout(token: String): String = authenticationProcessor.remove(token)

    fun delete(userKey: String) {
        authenticationProcessor.withdrawal(userKey)
    }

    fun updatePassword(
        userKey: String,
        updatePassword: UpdatePassword,
    ) {
        authenticationUpdater.updatePassword(userKey, updatePassword)
    }

    fun updatePassword(
        userKey: String,
        loginId: String,
        newPassword: String,
    ) {
        authenticationUpdater.updatePassword(userKey, loginId, newPassword)
    }

    fun updatePassword(
        userKey: String,
        newPassword: String,
    ) {
        authenticationUpdater.updatePassword(userKey, newPassword)
    }

    fun updateLoginId(
        authenticationId: Long,
        updateLoginId: UpdateLoginId,
    ) {
        authenticationUpdater.updateLoginId(authenticationId, updateLoginId)
    }

    fun getCredentialsByAuthenticationOfPts(credentialsPida: CredentialsPida): AuthenticationPida =
        authenticationReader.getCredentialsByAuthentication(credentialsPida)

    fun findLoginId(userKey: String): List<LoginIdWithSocialType> = authenticationReader.getLoginIdWithSocialTypes(userKey)

    fun findLoginIdAndSocialType(userKey: String): LoginIdWithSocialType = authenticationReader.getLoginIdWithSocialType(userKey)

    fun checkLoginId(loginId: String) {
        authenticationValidator.verifyLoginId(loginId)
    }
}
