package com.pida.jwt

import com.pida.auth.AuthenticationHistory
import com.pida.auth.AuthenticationHistoryReader
import com.pida.auth.AuthenticationHistoryUpdater
import com.pida.auth.ProviderDetail
import com.pida.auth.RedisTokenRepository
import com.pida.auth.TokenWithAuthentication
import com.pida.config.AuthenticationProperties
import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import com.pida.token.NewToken
import com.pida.token.Token
import com.pida.token.TokenStatus
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.LocalDateTime
import java.security.MessageDigest
import java.util.ArrayDeque
import java.util.concurrent.TimeUnit

class JwtProviderTest {
    private val jwtEncoder = QueueJwtEncoder()
    private val jwtDecoder = mockk<JwtDecoder>()
    private val redisTokenRepository = mockk<RedisTokenRepository>()
    private val authenticationHistoryReader = mockk<AuthenticationHistoryReader>()
    private val authenticationHistoryUpdater = mockk<AuthenticationHistoryUpdater>()
    private val redissonClient = mockk<RedissonClient>()
    private val lock = mockk<RLock>()

    private val authenticationProperties =
        AuthenticationProperties(
            accessTokenExpirationSeconds = 30,
            refreshTokenExpirationSeconds = 60,
        )

    private val jwtProvider =
        JwtProvider(
            jwtEncoder = jwtEncoder,
            jwtDecoder = jwtDecoder,
            authenticationProperties = authenticationProperties,
            redisTokenRepository = redisTokenRepository,
            authenticationHistoryReader = authenticationHistoryReader,
            authenticationHistoryUpdater = authenticationHistoryUpdater,
            redissonClient = redissonClient,
        )

    @BeforeEach
    fun setUp() {
        jwtEncoder.reset()
        listOf("old-refresh", "refresh-token", "missing-refresh").forEach { refreshToken ->
            every { redissonClient.getLock(lockKey(refreshToken)) } returns lock
        }
        every { lock.tryLock(3L, 10L, TimeUnit.SECONDS) } returns true
        every { lock.isHeldByCurrentThread } returns true
        every { lock.unlock() } just runs
    }

    @Test
    fun `이미 재발급이 완료된 refresh token 재요청은 최신 토큰을 그대로 반환한다`() {
        val latestToken =
            TokenWithAuthentication(
                accessToken = "latest-access",
                refreshToken = "latest-refresh",
                deviceId = "device-1",
                provider =
                    ProviderDetail(
                        userId = 1L,
                        userKey = "user-key",
                        grantedAuthorities = listOf("USER"),
                    ),
            )
        val latestHistory =
            authenticationHistory(
                accessToken = latestToken.accessToken,
                refreshToken = latestToken.refreshToken,
            )

        every { jwtDecoder.decode("old-refresh") } returns decodedJwt(tokenValue = "old-refresh", id = "user-key")
        every { redisTokenRepository.findByTokenOrNull("old-refresh") } returns latestToken
        every {
            authenticationHistoryReader.readByUserKeyWithDeviceWithRefreshToken(
                userKey = "user-key",
                deviceId = "device-1",
                refreshToken = "latest-refresh",
            )
        } returns latestHistory

        jwtProvider.renew("old-refresh") shouldBe Token("latest-access", "latest-refresh")

        jwtEncoder.encodeCount shouldBe 0
    }

    @Test
    fun `redis refresh token 이 유실되어도 active 이력이 남아 있으면 안전하게 재발급한다`() {
        val currentHistory =
            authenticationHistory(
                accessToken = "old-access",
                refreshToken = "refresh-token",
            )
        val newTokenWithAuthentication =
            TokenWithAuthentication(
                accessToken = "new-access",
                refreshToken = "new-refresh",
                deviceId = "device-1",
                provider =
                    ProviderDetail(
                        userId = 1L,
                        userKey = "user-key",
                        grantedAuthorities = listOf("USER"),
                    ),
            )
        val expectedUpdate =
            com.pida.auth.UpdateAuthenticationHistory(
                userKey = "user-key",
                deviceId = "device-1",
                refreshToken = "refresh-token",
                newToken =
                    NewToken(
                        token = Token("new-access", "new-refresh"),
                    ),
            )

        every { jwtDecoder.decode("refresh-token") } returns decodedJwt(tokenValue = "refresh-token", id = "user-key")
        every { redisTokenRepository.findByTokenOrNull("refresh-token") } returns null
        every {
            authenticationHistoryReader.readByUserKeyWithRefreshTokenOrNull(
                userKey = "user-key",
                refreshToken = "refresh-token",
            )
        } returns currentHistory
        every {
            authenticationHistoryReader.readByUserKeyWithDeviceWithRefreshToken(
                userKey = "user-key",
                deviceId = "device-1",
                refreshToken = "refresh-token",
            )
        } returns currentHistory
        jwtEncoder.enqueue("new-access", "new-refresh")
        every {
            redisTokenRepository.create(
                accessToken = "new-access",
                refreshToken = "new-refresh",
                deviceId = "device-1",
                providerDetail = newTokenWithAuthentication.provider,
                accessTokenExpiration = 30,
                refreshTokenExpiration = 60,
            )
        } returns newTokenWithAuthentication
        every { authenticationHistoryUpdater.update(expectedUpdate) } returns currentHistory
        every {
            redisTokenRepository.createRefreshAlias(
                refreshToken = "refresh-token",
                tokenWithAuthentication = newTokenWithAuthentication,
                expirationSeconds = 10,
            )
        } just runs
        every { redisTokenRepository.deleteToken("old-access") } just runs

        jwtProvider.renew("refresh-token") shouldBe Token("new-access", "new-refresh")

        verify(exactly = 1) {
            redisTokenRepository.createRefreshAlias(
                refreshToken = "refresh-token",
                tokenWithAuthentication = newTokenWithAuthentication,
                expirationSeconds = 10,
            )
        }
        verify(exactly = 1) { authenticationHistoryUpdater.update(expectedUpdate) }
        jwtEncoder.encodeCount shouldBe 2
    }

    @Test
    fun `redis 와 인증 이력 모두에 없는 refresh token 은 거절한다`() {
        every { jwtDecoder.decode("missing-refresh") } returns decodedJwt(tokenValue = "missing-refresh", id = "user-key")
        every { redisTokenRepository.findByTokenOrNull("missing-refresh") } returns null
        every {
            authenticationHistoryReader.readByUserKeyWithRefreshTokenOrNull(
                userKey = "user-key",
                refreshToken = "missing-refresh",
            )
        } returns null

        val exception =
            assertThrows(AuthenticationErrorException::class.java) {
                jwtProvider.renew("missing-refresh")
            }

        exception.authenticationErrorType shouldBe AuthenticationErrorType.INVALID_TOKEN
    }

    private fun decodedJwt(
        tokenValue: String,
        id: String,
    ): Jwt =
        mockk {
            every { this@mockk.tokenValue } returns tokenValue
            every { this@mockk.id } returns id
        }

    private fun encodedJwt(tokenValue: String): Jwt =
        mockk {
            every { this@mockk.tokenValue } returns tokenValue
        }

    private fun lockKey(refreshToken: String): String = "auth:refresh:renew:${refreshToken.sha256()}"

    private fun String.sha256(): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())
            .joinToString("") { "%02x".format(it) }

    private fun authenticationHistory(
        accessToken: String,
        refreshToken: String,
    ): AuthenticationHistory =
        AuthenticationHistory(
            authenticationId = 1L,
            userId = 1L,
            userKey = "user-key",
            deviceId = "device-1",
            token =
                Token(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                ),
            status = TokenStatus.ACTIVE,
            loggedInAt = LocalDateTime.of(2026, 3, 22, 10, 0),
        )

    private inner class QueueJwtEncoder : JwtEncoder {
        private val tokenValues = ArrayDeque<String>()

        var encodeCount: Int = 0
            private set

        fun enqueue(vararg values: String) {
            values.forEach(tokenValues::addLast)
        }

        fun reset() {
            tokenValues.clear()
            encodeCount = 0
        }

        override fun encode(parameters: JwtEncoderParameters): Jwt {
            encodeCount += 1
            return encodedJwt(tokenValues.removeFirst())
        }
    }
}
