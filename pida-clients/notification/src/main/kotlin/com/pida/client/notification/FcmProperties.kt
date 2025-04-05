package com.pida.client.notification

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fcm")
data class FcmProperties(
    var keyJson: String,
)
