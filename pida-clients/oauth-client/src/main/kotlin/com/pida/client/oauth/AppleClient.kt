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
import org.springframework.stereotype.Component
import java.text.ParseException
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
        // audience 확인 부분 개선
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
}
