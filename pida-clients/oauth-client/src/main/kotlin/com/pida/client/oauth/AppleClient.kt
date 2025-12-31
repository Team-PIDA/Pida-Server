package com.pida.client.oauth

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Base64
import java.util.Date

@Component
class AppleClient internal constructor(
    private val appleApi: AppleApi,
    private val appleProperties: AppleProperties,
    private val objectMapper: ObjectMapper,
) {
    fun getUserInfo(token: String): AppleClientResult {
        val signedJWT: SignedJWT
        val jwtClaims: JWTClaimsSet
        try {
            signedJWT = SignedJWT.parse(token)
            jwtClaims = signedJWT.jwtClaimsSet
        } catch (e: ParseException) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
        }

        return try {
            AppleClientResult(
                jwtClaims.getStringClaim("sub"),
                jwtClaims.getStringClaim("email"),
            )
        } catch (e: ParseException) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
        }
    }

    fun revoke(token: String) {
        val clientSecret = generateAppleClientSecret()

        appleApi.revokeToken(
            appleProperties.bundleId,
            clientSecret,
            token,
        )
    }

    fun verify(token: String): Boolean {
        val signedJWT: SignedJWT
        val jwtClaims: JWTClaimsSet
        try {
            signedJWT = SignedJWT.parse(token)
            jwtClaims = signedJWT.jwtClaimsSet
        } catch (e: ParseException) {
            return false
        }

        if (!isSignatureValid(signedJWT)) {
            return false
        }
        val currentDate = Date(System.currentTimeMillis())
        val bundleId = jwtClaims.audience.firstOrNull()
        if (bundleId != appleProperties.bundleId) return false

        val appleUrl = jwtClaims.issuer
        return currentDate.before(jwtClaims.expirationTime) && appleUrl == "https://appleid.apple.com"
    }

    private fun isSignatureValid(signedJWT: SignedJWT): Boolean {
        val appleKeys = appleApi.getApplePublicKeys().keys
        for (key in appleKeys) {
            try {
                val rsaKey = JWK.parse(objectMapper.writeValueAsString(key)) as RSAKey
                val publicKey = rsaKey.toRSAPublicKey()
                val verifier = RSASSAVerifier(publicKey)
                if (signedJWT.verify(verifier)) {
                    return true
                }
            } catch (e: JsonProcessingException) {
                throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
            } catch (e: ParseException) {
                throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
            } catch (e: JOSEException) {
                throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
            }
        }
        return false
    }

    fun generateAppleClientSecret(): String {
        val expirationDate =
            Date.from(
                LocalDateTime
                    .now()
                    .plusMinutes(5)
                    .atZone(ZoneId.systemDefault())
                    .toInstant(),
            )

        val teamId = appleProperties.teamId
        val clientId = appleProperties.bundleId
        val keyId = appleProperties.keyId

        val now = Date()

        val jwtBuilder =
            Jwts
                .builder()
                .header()
                .add("kid", keyId)
                .add("alg", "ES256")
                .and()
                .issuer(teamId)
                .issuedAt(now)
                .expiration(expirationDate)
                .audience()
                .add("https://appleid.apple.com")
                .and()
                .subject(clientId)

        return jwtBuilder
            .signWith(getPrivateKey())
            .compact()
    }

    fun getPrivateKey(): PrivateKey {
        val p8 = appleProperties.privateKey
        val keyContent =
            Files
                .readAllLines(Paths.get(p8))
                .filterNot { it.startsWith("-----") }
                .joinToString("")

        val decoded = Base64.getDecoder().decode(keyContent)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("EC")

        return keyFactory.generatePrivate(keySpec)
    }
}
