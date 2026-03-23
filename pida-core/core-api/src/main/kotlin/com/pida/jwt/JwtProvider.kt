package com.pida.jwt

import com.pida.auth.AuthenticationHistory
import com.pida.auth.AuthenticationHistoryReader
import com.pida.auth.AuthenticationHistoryUpdater
import com.pida.auth.AuthorityType
import com.pida.auth.GrantedAuthority
import com.pida.auth.Provider
import com.pida.auth.ProviderDetail
import com.pida.auth.RedisTokenRepository
import com.pida.auth.TokenWithAuthentication
import com.pida.auth.UpdateAuthenticationHistory
import com.pida.config.AuthenticationProperties
import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import com.pida.token.NewToken
import com.pida.token.Token
import com.pida.token.TokenStatus
import com.pida.token.repository.TokenRepository
import com.pida.user.SocialUser
import com.pida.user.User
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.naming.AuthenticationException

@Component
class JwtProvider(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val authenticationProperties: AuthenticationProperties,
    private val redisTokenRepository: RedisTokenRepository,
    private val authenticationHistoryReader: AuthenticationHistoryReader,
    private val authenticationHistoryUpdater: AuthenticationHistoryUpdater,
    @param:Qualifier("authRedissonClient")
    private val redissonClient: RedissonClient,
) : TokenRepository {
    companion object {
        val grantedAuthorities = listOf(GrantedAuthority(AuthorityType.USER))
        private const val REFRESH_RENEW_LOCK_KEY_PREFIX = "auth:refresh:renew"
        private const val REFRESH_RENEW_LOCK_WAIT_SECONDS = 3L
        private const val REFRESH_RENEW_LOCK_LEASE_SECONDS = 10L
        private const val REFRESH_TOKEN_ROTATION_GRACE_SECONDS = 10L
    }

    override fun create(
        deviceId: String?,
        user: User,
    ): Token {
        val accessToken =
            issueAccessToken(
                user.key,
                grantedAuthorities,
            )
        val refreshToken = issueRefreshToken(user.key)
        return Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
        ).apply {
            redisTokenRepository.create(
                accessToken = this.accessToken,
                refreshToken = this.refreshToken,
                deviceId = deviceId,
                providerDetail =
                    ProviderDetail(
                        user.id,
                        user.key,
                        grantedAuthorities =
                            grantedAuthorities.map {
                                it.authorityType.name
                            },
                    ),
                accessTokenExpiration = authenticationProperties.accessTokenExpirationSeconds,
                refreshTokenExpiration = authenticationProperties.refreshTokenExpirationSeconds,
            )
        }
    }

    override fun create(
        deviceId: String?,
        socialUser: SocialUser,
    ): Token {
        val accessToken =
            issueAccessToken(
                socialUser.key,
                grantedAuthorities,
            )
        val refreshToken = issueRefreshToken(socialUser.key)
        return Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
        ).apply {
            redisTokenRepository.create(
                accessToken = this.accessToken,
                refreshToken = this.refreshToken,
                deviceId = deviceId,
                providerDetail =
                    ProviderDetail(
                        socialUser.id,
                        socialUser.key,
                        grantedAuthorities =
                            grantedAuthorities.map {
                                it.authorityType.name
                            },
                    ),
                accessTokenExpiration = authenticationProperties.accessTokenExpirationSeconds,
                refreshTokenExpiration = authenticationProperties.refreshTokenExpirationSeconds,
            )
        }
    }

    override fun renew(refreshToken: String): Token {
        val jwt = validateToken(refreshToken)
        return runWithRefreshRenewLock(jwt.tokenValue) {
            val tokenWithAuthentication = findRenewableToken(jwt)
            val authenticationHistory =
                verifyTokenHistory(
                    userKey = tokenWithAuthentication.provider.userKey,
                    deviceId = tokenWithAuthentication.deviceId,
                    refreshToken = tokenWithAuthentication.refreshToken,
                )

            if (tokenWithAuthentication.refreshToken != jwt.tokenValue) {
                return@runWithRefreshRenewLock Token(
                    accessToken = tokenWithAuthentication.accessToken,
                    refreshToken = tokenWithAuthentication.refreshToken,
                )
            }

            val renewedToken =
                Token(
                    accessToken =
                        issueAccessToken(
                            jwtId = tokenWithAuthentication.provider.userKey,
                            grantedAuthorities =
                                tokenWithAuthentication.provider.grantedAuthorities.map {
                                    GrantedAuthority(AuthorityType.valueOf(it))
                                },
                        ),
                    refreshToken =
                        issueRefreshToken(
                            jwtId = tokenWithAuthentication.provider.userKey,
                        ),
                )

            val renewedTokenWithAuthentication =
                TokenWithAuthentication(
                    accessToken = renewedToken.accessToken,
                    refreshToken = renewedToken.refreshToken,
                    deviceId = tokenWithAuthentication.deviceId,
                    provider = tokenWithAuthentication.provider,
                )

            redisTokenRepository.create(
                accessToken = renewedToken.accessToken,
                refreshToken = renewedToken.refreshToken,
                deviceId = tokenWithAuthentication.deviceId,
                providerDetail = tokenWithAuthentication.provider,
                accessTokenExpiration = authenticationProperties.accessTokenExpirationSeconds,
                refreshTokenExpiration = authenticationProperties.refreshTokenExpirationSeconds,
            )

            try {
                authenticationHistoryUpdater.update(
                    UpdateAuthenticationHistory(
                        userKey = authenticationHistory.userKey,
                        deviceId = authenticationHistory.deviceId,
                        refreshToken = authenticationHistory.token.refreshToken,
                        newToken = NewToken(token = renewedToken),
                    ),
                )
            } catch (exception: RuntimeException) {
                rollbackRenewedToken(renewedTokenWithAuthentication)
                throw exception
            }

            if (jwt.tokenValue != renewedToken.refreshToken) {
                runCatching {
                    redisTokenRepository.createRefreshAlias(
                        refreshToken = jwt.tokenValue,
                        tokenWithAuthentication = renewedTokenWithAuthentication,
                        expirationSeconds = REFRESH_TOKEN_ROTATION_GRACE_SECONDS,
                    )
                }
            }
            runCatching { redisTokenRepository.deleteToken(tokenWithAuthentication.accessToken) }
            renewedToken
        }
    }

    override fun remove(token: String): String {
        val jwt = validateToken(token)
        authenticationHistoryUpdater.remove(token).apply {
            redisTokenRepository.deleteAllToken(this)
        }
        return jwt.id
    }

    override fun removeByUserKey(userKey: String) {
        authenticationHistoryUpdater.removeToken(userKey).map {
            redisTokenRepository.deleteAllToken(it)
        }
    }

    override fun findBy(accessToken: String): Provider? = redisTokenRepository.findBy(accessToken)

    @Throws(AuthenticationException::class)
    fun validateToken(token: String): Jwt =
        try {
            jwtDecoder.decode(token)
        } catch (exception: BadJwtException) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_TOKEN)
        } catch (exception: JwtException) {
            throw AuthenticationServiceException(exception.message, exception)
        }

    /**
     * issue access token
     *
     * @param jwtId [String] jwt identifier.
     */
    private fun issueAccessToken(
        jwtId: String,
        grantedAuthorities: List<GrantedAuthority>,
    ): String {
        val issuedAt: Instant = Instant.now()
        return generateToken(
            jwtId = jwtId,
            expiresAt = issuedAt.plusSeconds(authenticationProperties.accessTokenExpirationSeconds * 60L),
            issuedAt = issuedAt,
            claims = mapOf(Pair("type", "A"), Pair("roles", grantedAuthorities.map { it.authorityType.name })),
        )
    }

    /**
     * issue refresh token
     *
     * @param jwtId [String] jwt identifier.
     */
    private fun issueRefreshToken(jwtId: String): String {
        val issuedAt: Instant = Instant.now()
        return generateToken(
            jwtId = jwtId,
            expiresAt = issuedAt.plusSeconds(authenticationProperties.refreshTokenExpirationSeconds * 60L),
            issuedAt = issuedAt,
            claims = mapOf(Pair("type", "R")),
        )
    }

    /**
     * Generate Token
     *
     * @param jwtId [String] jwt identifier.
     * @param expiresAt [Instant] token's expiresAt.
     * @param issuedAt [Instant] token's issuedAt.
     * @param claims [Map] token's claims.
     */
    private fun generateToken(
        jwtId: String,
        expiresAt: Instant,
        issuedAt: Instant,
        claims: Map<String, Any>? = emptyMap(),
    ): String {
        val jwtClaimsSet: JwtClaimsSet =
            JwtClaimsSet
                .builder()
                .id(jwtId)
                .expiresAt(expiresAt)
                .issuedAt(issuedAt)
                .issuer("pida")
                .claims {
                    it["tokenId"] = UUID.randomUUID().toString()
                    if (claims != null) {
                        it.putAll(claims)
                    }
                }.build()
        try {
            return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).tokenValue
        } catch (e: IllegalArgumentException) {
            throw InvalidBearerTokenException(e.message)
        }
    }

    private fun verifyTokenHistory(
        userKey: String,
        deviceId: String?,
        refreshToken: String,
    ): AuthenticationHistory {
        val authenticationHistory =
            authenticationHistoryReader.readByUserKeyWithDeviceWithRefreshToken(
                userKey = userKey,
                deviceId = deviceId,
                refreshToken = refreshToken,
            )

        if (authenticationHistory.status == TokenStatus.INACTIVE) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_TOKEN)
        }
        return authenticationHistory
    }

    private fun findRenewableToken(jwt: Jwt): TokenWithAuthentication =
        redisTokenRepository.findByTokenOrNull(jwt.tokenValue) ?: restoreRenewableToken(jwt)

    private fun restoreRenewableToken(jwt: Jwt): TokenWithAuthentication {
        val authenticationHistory =
            authenticationHistoryReader.readByUserKeyWithRefreshTokenOrNull(
                userKey = jwt.id,
                refreshToken = jwt.tokenValue,
            ) ?: throw AuthenticationErrorException(AuthenticationErrorType.INVALID_TOKEN)

        return TokenWithAuthentication(
            accessToken = authenticationHistory.token.accessToken,
            refreshToken = authenticationHistory.token.refreshToken,
            deviceId = authenticationHistory.deviceId,
            provider =
                ProviderDetail(
                    userId = authenticationHistory.userId,
                    userKey = authenticationHistory.userKey,
                    grantedAuthorities = grantedAuthorities.map { it.authorityType.name },
                ),
        )
    }

    private fun rollbackRenewedToken(tokenWithAuthentication: TokenWithAuthentication) {
        runCatching {
            redisTokenRepository.deleteToken(tokenWithAuthentication.accessToken)
            redisTokenRepository.deleteToken(tokenWithAuthentication.refreshToken)
        }
    }

    private fun runWithRefreshRenewLock(
        refreshToken: String,
        action: () -> Token,
    ): Token {
        val lock =
            runCatching {
                redissonClient.getLock(refreshRenewLockKey(refreshToken))
            }.getOrElse {
                return action()
            }

        val acquired =
            try {
                lock.tryLock(REFRESH_RENEW_LOCK_WAIT_SECONDS, REFRESH_RENEW_LOCK_LEASE_SECONDS, TimeUnit.SECONDS)
            } catch (exception: InterruptedException) {
                Thread.currentThread().interrupt()
                return action()
            } catch (exception: RuntimeException) {
                return action()
            }

        if (!acquired) {
            return action()
        }

        return try {
            action()
        } finally {
            runCatching {
                if (lock.isHeldByCurrentThread) {
                    lock.unlock()
                }
            }
        }
    }

    private fun refreshRenewLockKey(refreshToken: String): String = "$REFRESH_RENEW_LOCK_KEY_PREFIX:${refreshToken.sha256()}"

    private fun String.sha256(): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
