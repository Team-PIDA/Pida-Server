package com.pida.authentication.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@ConfigurationProperties(prefix = "rsa")
data class RsaKeyProperties(
    val publicKey: File,
    val privateKey: File,
) {
    fun getRSAPublicKey(): RSAPublicKey {
        val publicKeyPEM =
            String(Files.readAllBytes(publicKey.toPath()), Charset.defaultCharset())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace(System.lineSeparator(), "")

        val encoded: ByteArray = Base64.getDecoder().decode(publicKeyPEM)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(encoded)
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    fun getRSAPrivateKey(): RSAPrivateKey {
        val privateKeyPem =
            String(Files.readAllBytes(privateKey.toPath()), Charset.defaultCharset())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace(System.lineSeparator(), "")
        val encoded: ByteArray = Base64.getDecoder().decode(privateKeyPem)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(encoded)
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }
}
