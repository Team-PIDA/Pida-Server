package com.pida.authentication.domain.auth.component

import com.pida.authentication.domain.auth.AuthenticationPida
import com.pida.authentication.domain.auth.AuthenticationSns
import com.pida.authentication.domain.auth.CredentialSocial
import com.pida.authentication.domain.auth.CredentialsPida
import com.pida.authentication.domain.auth.NewAuthenticationHistory
import com.pida.authentication.domain.auth.NewAuthenticationSocial
import com.pida.authentication.domain.auth.repository.AuthenticationRepository
import com.pida.authentication.domain.token.NewToken
import com.pida.authentication.domain.token.Token
import com.pida.authentication.domain.token.TokenStatus
import com.pida.authentication.domain.token.repository.TokenRepository
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import org.springframework.stereotype.Component

@Component
class AuthenticationProcessor(
    private val authenticationRepository: AuthenticationRepository,
    private val tokenRepository: TokenRepository,
    private val authenticationValidator: AuthenticationValidator,
    private val authenticationHistoryWriter: AuthenticationHistoryWriter,
) {
    fun login(
        deviceId: String,
        credentialsPida: CredentialsPida,
    ): Token {
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

        return tokenRepository.create(deviceId, authentication).apply {
            authenticationHistoryWriter.write(
                NewAuthenticationHistory(
                    userId = authentication.userId,
                    userKey = authentication.userKey,
                    deviceId = deviceId,
                    newToken =
                        NewToken(
                            Token(
                                accessToken = this.accessToken,
                                refreshToken = this.refreshToken,
                            ),
                        ),
                    status = TokenStatus.ACTIVE,
                ),
            )
        }
    }

    fun login(
        deviceId: String,
        credentialSocial: CredentialSocial,
    ): Token {
        val authentication: AuthenticationSns =
            authenticationRepository.findBy(
                socialId = credentialSocial.socialId,
                socialType = credentialSocial.socialType,
            ) ?: throw AuthenticationErrorException(AuthenticationErrorType.INVALID_CREDENTIALS)

        authenticationValidator.verifyAuthenticationStatus(
            authenticationStatus = authentication.authenticationStatus,
        )

        return tokenRepository.create(deviceId, authentication).apply {
            authenticationHistoryWriter.write(
                NewAuthenticationHistory(
                    userId = authentication.userId,
                    userKey = authentication.userKey,
                    deviceId = deviceId,
                    newToken =
                        NewToken(
                            Token(
                                accessToken = this.accessToken,
                                refreshToken = this.refreshToken,
                            ),
                        ),
                    status = TokenStatus.ACTIVE,
                ),
            )
        }
    }

    fun createAuthentication(
        userId: Long,
        userKey: String,
        newAuthenticationSocial: NewAuthenticationSocial,
    ): AuthenticationSns =
        authenticationRepository.createAuthentication(
            userId = userId,
            userKey = userKey,
            newAuthenticationSocial = newAuthenticationSocial,
        )

    fun renew(refreshToken: String): Token = tokenRepository.renew(refreshToken)

    fun remove(token: String): String = tokenRepository.remove(token)

    fun withdrawal(userKey: String) {
        authenticationRepository.withdrawal(userKey)
        tokenRepository.removeByUserKey(userKey)
    }
}
