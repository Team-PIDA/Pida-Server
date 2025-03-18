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
import java.util.Base64

@ConfigurationProperties(prefix = "rsa")
data class RsaKeyProperties(
    val publicKey: RSAPublicKey,
    val privateKey: RSAPrivateKey,
)