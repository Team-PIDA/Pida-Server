package com.pida.authentication.client.oauth

import org.springframework.stereotype.Component

@Component
class KaKaoClient internal constructor(
    private val kaKaoApi: KaKaoApi,
) {
    fun getUserInfo(token: String): KaKaoClientResult = kaKaoApi.getKaKaoUserInfo("Bearer $token").toResult()
}
