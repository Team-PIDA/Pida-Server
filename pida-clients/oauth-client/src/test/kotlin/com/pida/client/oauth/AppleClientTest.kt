package com.pida.client.oauth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.pida.client.oauth.response.ApplePublicKeysResponse
import com.pida.client.oauth.response.Key
import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Date

class AppleClientTest {
    @Test
    fun `apple public key 조회가 실패하면 캐시된 jwk로 검증한다`() {
        val appleApi = mockk<AppleApi>()
        val applePublicKeysCache = mockk<ApplePublicKeysCache>()
        val externalDependencyPolicy = mockk<ExternalDependencyPolicy>()
        every { applePublicKeysCache.getOrLoad(any()) } throws IllegalStateException("apple down")
        every { applePublicKeysCache.getCachedOrNull() } returns cachedKeys()
        every { externalDependencyPolicy.recordFallback(any(), any(), any()) } just runs

        val client =
            AppleClient(
                appleApi = appleApi,
                appleProperties = AppleProperties(bundleId = "com.pida.app"),
                applePublicKeysCache = applePublicKeysCache,
                externalDependencyPolicy = externalDependencyPolicy,
                objectMapper = jacksonObjectMapper(),
            )

        client.verify(signedAppleIdentityToken()) shouldBe true
        verify(exactly = 1) {
            externalDependencyPolicy.recordFallback(ExternalDependency.APPLE_AUTH, "cached-jwk", any())
        }
    }

    private fun signedAppleIdentityToken(): String {
        val claims =
            JWTClaimsSet
                .Builder()
                .subject("apple-user")
                .claim("email", "apple@example.com")
                .audience("com.pida.app")
                .issuer("https://appleid.apple.com")
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .issueTime(Date.from(Instant.now()))
                .build()

        val signedJwt =
            SignedJWT(
                JWSHeader
                    .Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .keyID(rsaKey.keyID)
                    .build(),
                claims,
            )

        signedJwt.sign(RSASSASigner(rsaKey.toPrivateKey()))
        return signedJwt.serialize()
    }

    private fun cachedKeys(): ApplePublicKeysResponse {
        val publicKey = rsaKey.toPublicJWK() as RSAKey
        val publicKeyJson = publicKey.toJSONObject()
        return ApplePublicKeysResponse(
            keys =
                listOf(
                    Key(
                        kty = publicKey.keyType.value,
                        kid = publicKey.keyID,
                        use = publicKey.keyUse.identifier(),
                        alg = publicKey.algorithm.name,
                        n = publicKeyJson.getValue("n").toString(),
                        e = publicKeyJson.getValue("e").toString(),
                    ),
                ),
        )
    }

    companion object {
        private val rsaKey: RSAKey =
            RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID("apple-key-1")
                .generate()
    }
}
