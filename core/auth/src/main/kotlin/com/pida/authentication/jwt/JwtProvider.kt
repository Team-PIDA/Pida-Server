package com.pida.authentication.jwt

import com.pida.authentication.config.AuthenticationProperties
import com.pida.authentication.domain.auth.Authentication
import com.pida.authentication.domain.auth.AuthenticationHistory
import com.pida.authentication.domain.auth.AuthorityType
import com.pida.authentication.domain.auth.GrantedAuthority
import com.pida.authentication.domain.auth.Provider
import com.pida.authentication.domain.auth.UpdateAuthenticationHistory
import com.pida.authentication.domain.auth.component.AuthenticationHistoryReader
import com.pida.authentication.domain.auth.component.AuthenticationHistoryUpdater
import com.pida.authentication.domain.token.NewToken
import com.pida.authentication.domain.token.PhoneToken
import com.pida.authentication.domain.token.Token
import com.pida.authentication.domain.token.TokenStatus
import com.pida.authentication.domain.token.repository.TokenRepository
import com.pida.authentication.storage.redis.auth.RedisTokenRepository
import com.pida.authentication.storage.redis.provider.ProviderDetail
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
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
import java.time.Instant
import javax.naming.AuthenticationException

@Component
class JwtProvider(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val authenticationProperties: AuthenticationProperties,
    private val redisTokenRepository: RedisTokenRepository,
    private val authenticationHistoryReader: AuthenticationHistoryReader,
    private val authenticationHistoryUpdater: AuthenticationHistoryUpdater,
) : TokenRepository {
    override fun create(
        deviceId: String,
        authentication: Authentication,
    ): Token {
        val accessToken =
            issueAccessToken(
                authentication.userKey,
                authentication.grantedAuthorities,
            )
        val refreshToken = issueRefreshToken(authentication.userKey)
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
                        authentication.id,
                        authentication.userId,
                        authentication.userKey,
                        grantedAuthorities =
                            authentication.grantedAuthorities.map {
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
        val tokenWithAuthentication = redisTokenRepository.findByToken(jwt.tokenValue)
        val authenticationHistory =
            verifyTokenHistory(
                userKey = tokenWithAuthentication.provider.userKey,
                deviceId = tokenWithAuthentication.deviceId,
                refreshToken = tokenWithAuthentication.refreshToken,
            )

        removeRotationToken(tokenWithAuthentication.accessToken, tokenWithAuthentication.refreshToken)

        val newAccessToken =
            issueAccessToken(
                jwtId = tokenWithAuthentication.provider.userKey,
                grantedAuthorities =
                    tokenWithAuthentication.provider.grantedAuthorities.map {
                        GrantedAuthority(AuthorityType.valueOf(it))
                    },
            )
        val newRefreshToken =
            issueRefreshToken(
                jwtId = tokenWithAuthentication.provider.userKey,
            )

        return Token(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        ).apply {
            redisTokenRepository.create(
                accessToken = this.accessToken,
                refreshToken = this.refreshToken,
                deviceId = tokenWithAuthentication.deviceId,
                providerDetail = tokenWithAuthentication.provider,
                accessTokenExpiration = authenticationProperties.accessTokenExpirationSeconds,
                refreshTokenExpiration = authenticationProperties.refreshTokenExpirationSeconds,
            )

            authenticationHistoryUpdater.update(
                UpdateAuthenticationHistory(
                    userKey = authenticationHistory.userKey,
                    deviceId = authenticationHistory.deviceId,
                    refreshToken = refreshToken,
                    newToken =
                        NewToken(
                            token =
                                Token(
                                    accessToken = this.accessToken,
                                    refreshToken = this.refreshToken,
                                ),
                        ),
                ),
            )
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

    override fun createPhoneJwt(phone: String): String = issuePhoneMessageToken(phone = phone)

    @Throws(AuthenticationException::class)
    override fun getPhoneWithVerifyPhoneJwt(phoneToken: PhoneToken): String =
        try {
            jwtDecoder.decode(phoneToken.token).id
        } catch (exception: BadJwtException) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_TOKEN)
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
     * issue phone message token
     *
     * @param phone [String] phone number.
     */
    private fun issuePhoneMessageToken(phone: String): String {
        val issuedAt: Instant = Instant.now()
        return generateToken(
            jwtId = phone,
            expiresAt = issuedAt.plusSeconds(180 * 60L),
            issuedAt = issuedAt,
            claims = mapOf(Pair("type", "P")),
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
        deviceId: String,
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

    private fun removeRotationToken(
        accessToken: String,
        refreshToken: String,
    ) {
        redisTokenRepository.deleteToken(accessToken)
        redisTokenRepository.deleteToken(refreshToken)
    }
}
