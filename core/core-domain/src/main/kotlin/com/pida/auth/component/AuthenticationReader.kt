package com.pida.auth.component

import com.pida.auth.CredentialsPida
import com.pida.auth.LoginIdWithSocialType
import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import org.springframework.stereotype.Component

@Component
class AuthenticationReader(
    private val authenticationRepository: AuthenticationRepository,
    private val authenticationValidator: AuthenticationValidator,
) {
    fun getLoginIdWithSocialTypes(userKey: String): List<LoginIdWithSocialType> =
        authenticationRepository.readLoginId(userKey)
            ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_DATA)

    fun getLoginIdWithSocialType(userKey: String): LoginIdWithSocialType =
        authenticationRepository.readyByLoginIdWithSocialType(userKey)
            ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_DATA)

    fun getCredentialsByAuthentication(credentialsPida: CredentialsPida): AuthenticationPida {
        val authentication: AuthenticationPida =
            authenticationRepository.findBy(
                loginId = credentialsPida.loginId,
            ) ?: throw AuthenticationErrorException(AuthenticationErrorType.INVALID_CREDENTIALS)

        authenticationValidator.verifyPassword(
            password = credentialsPida.password,
            encryptedPassword = authentication.password,
        )

        authenticationValidator.verifyAuthenticationStatus(
            authenticationStatus = authentication.authenticationStatus,
        )

        return authentication
    }
}
