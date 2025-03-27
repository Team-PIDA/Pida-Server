package com.pida.auth

import com.pida.token.NewToken
import com.pida.token.Token
import com.pida.token.TokenStatus
import com.pida.token.repository.TokenRepository
import com.pida.user.SocialUser
import com.pida.user.User
import org.springframework.stereotype.Component

@Component
class AuthenticationProcessor(
    private val tokenRepository: TokenRepository,
    private val authenticationHistoryWriter: AuthenticationHistoryWriter,
) {
    fun login(
        deviceId: String?,
        user: User,
        credentialsPida: CredentialsPida,
    ): Token =
        tokenRepository.create(deviceId, user).apply {
            authenticationHistoryWriter.write(
                NewAuthenticationHistory(
                    userId = user.id,
                    userKey = user.key,
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

    fun login(
        deviceId: String,
        socialUser: SocialUser,
    ): Token =
        tokenRepository.create(deviceId, socialUser).apply {
            authenticationHistoryWriter.write(
                NewAuthenticationHistory(
                    userId = socialUser.id,
                    userKey = socialUser.key,
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

    fun renew(refreshToken: String): Token = tokenRepository.renew(refreshToken)

    fun remove(token: String): String = tokenRepository.remove(token)

    fun withdrawal(userKey: String) {
        tokenRepository.removeByUserKey(userKey)
    }
}
