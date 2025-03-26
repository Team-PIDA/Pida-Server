package com.pida.auth.component

import com.pida.auth.*
import com.pida.token.NewToken
import com.pida.token.Token
import com.pida.token.TokenStatus
import com.pida.token.repository.TokenRepository
import org.springframework.stereotype.Component

@Component
class AuthenticationProcessor(
//    private val authenticationRepository: AuthenticationRepository,
    private val tokenRepository: TokenRepository,
    private val authenticationValidator: AuthenticationValidator,
    private val authenticationHistoryWriter: AuthenticationHistoryWriter,
) {
    fun login(
        deviceId: String?,
        credentialsPida: CredentialsPida,
    ): Token {
//        val authentication: AuthenticationPida =
//            authenticationRepository.findBy(
//                loginId = credentialsPida.loginId,
//            ) ?: throw AuthenticationErrorException(AuthenticationErrorType.INVALID_CREDENTIALS)

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
//        val authentication: AuthenticationSns =
//            authenticationRepository.findBy(
//                socialId = credentialSocial.socialId,
//                socialType = credentialSocial.socialType,
//            ) ?: throw AuthenticationErrorException(AuthenticationErrorType.INVALID_CREDENTIALS)

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

    fun createAuthentication(
        userId: Long,
        userKey: String,
        newAuthenticationPida: NewAuthenticationPida,
    ): AuthenticationPida =
        authenticationRepository.createAuthentication(
            userId = userId,
            userKey = userKey,
            newAuthenticationPida =
            newAuthenticationPida,
//                    .copy(password = passwordEncoder.encode(newAuthenticationPida.password)),
        )

    fun renew(refreshToken: String): Token = tokenRepository.renew(refreshToken)

    fun remove(token: String): String = tokenRepository.remove(token)

    fun withdrawal(userKey: String) {
        authenticationRepository.withdrawal(userKey)
        tokenRepository.removeByUserKey(userKey)
    }
}
