package com.pida.client.oauth

import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import org.springframework.stereotype.Component

@Component
class KaKaoClient internal constructor(
    private val kaKaoApi: KaKaoApi,
    private val externalDependencyPolicy: ExternalDependencyPolicy,
) {
    fun getUserInfo(token: String): KaKaoClientResult =
        externalDependencyPolicy.execute(ExternalDependency.KAKAO_OAUTH) {
            kaKaoApi.getKaKaoUserInfo("Bearer $token").toResult()
        }
}
