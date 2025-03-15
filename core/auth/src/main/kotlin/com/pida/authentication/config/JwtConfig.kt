package com.pida.authentication.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

@Configuration
class JwtConfig(
    private val rsaKeyProperties: RsaKeyProperties,
) {
    @Bean
    fun jwtDecoder(): JwtDecoder =
        NimbusJwtDecoder
            .withPublicKey(rsaKeyProperties.publicKey)
            .build()

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val rsaKey: RSAKey =
            RSAKey
                .Builder(rsaKeyProperties.publicKey)
                .privateKey(rsaKeyProperties.privateKey)
                .build()
        return NimbusJwtEncoder(ImmutableJWKSet(JWKSet(rsaKey)))
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaPublicKey: RSAPublicKey = rsaKeyProperties.publicKey
        val rsaPrivateKey: RSAPrivateKey = rsaKeyProperties.privateKey
        val rsaKey: RSAKey =
            RSAKey
                .Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .keyID(UUID.randomUUID().toString())
                .build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }
}
