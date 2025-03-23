package com.pida.authentication.client.oauth

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.pida.authentication.client.oauth.request.AppleTokenRequest
import com.pida.authentication.client.oauth.response.AppleTokenResponse
import com.pida.authentication.domain.auth.AppleClientResult
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import io.jsonwebtoken.Jwts
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.security.PrivateKey
import java.security.Security
import java.text.ParseException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Base64
import java.util.Date

@Component
class AppleClient internal constructor(
    private val appleApi: AppleApi,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val APPLE_BUNDLE_ID = "com.pida.me.ios"
        const val APPLE_URI = "https://appleid.apple.com"
    }

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
        if (bundleId != APPLE_BUNDLE_ID) return false

        val appleUrl = jwtClaims.issuer
        return currentDate.before(jwtClaims.expirationTime) && appleUrl == APPLE_URI
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

    // apple server에서 받아온 id_token
    private fun getAppleToken(appleTokenRequest: AppleTokenRequest): AppleTokenResponse {
        // Prepare form data
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add("client_id", appleTokenRequest.clientId)
        formData.add("client_secret", appleTokenRequest.clientSecret)
        formData.add("code", appleTokenRequest.code)
        formData.add("grant_type", appleTokenRequest.grantType)

        val tokenResponse =
            appleApi.getAppleToken(
                headers = mapOf(HttpHeaders.CONTENT_TYPE to "application/x-www-form-urlencoded"),
                body = formData.toSingleValueMap(),
            )

        return tokenResponse
    }

    private val privateKey: PrivateKey
        get() {
            Security.addProvider(BouncyCastleProvider())
            val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider("BC")

            try {
                val privateKeyBytes: ByteArray = Base64.getDecoder().decode("p8")
                val privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes)
                return converter.getPrivateKey(privateKeyInfo)
            } catch (e: Exception) {
                throw AuthenticationErrorException(AuthenticationErrorType.APPLE_PRIVATE_KEY_ENCODING_FAILED)
            }
        }

    private fun generateAppleClientSecret(): String {
        val expirationTime =
            LocalDateTime
                .now()
                .plusMinutes(5)
                .atZone(ZoneId.systemDefault())
                .toInstant()

        val jwtHeader =
            mapOf(
                "kid" to "dummyKeyId",
            )

        val jwtClaims =
            Jwts
                .claims()
                .issuer("dummyTeamId") // split('.') 필요 없으면 제거
                .issuedAt(Date.from(Instant.now()))
                .subject("dummyClientId")
                .expiration(Date.from(expirationTime))
                .audience()
                .add("https://appleid.apple.com")
                .and()
                .build()

        return Jwts
            .builder()
            .header()
            .add(jwtHeader)
            .and()
            .claims(jwtClaims)
            .signWith(privateKey)
            .compact()
    }
}
