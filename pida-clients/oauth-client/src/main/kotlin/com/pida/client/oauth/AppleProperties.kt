package com.pida.client.oauth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("apple")
data class AppleProperties(
    val bundleId: String,
)
