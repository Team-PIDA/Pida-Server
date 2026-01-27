package com.pida.client.map

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients
@EnableConfigurationProperties(KakaoMapProperties::class)
internal class MapFeignConfig
