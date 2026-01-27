package com.pida.client.map

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kakao")
data class KakaoMapProperties(
    val restApiKey: String,
)
