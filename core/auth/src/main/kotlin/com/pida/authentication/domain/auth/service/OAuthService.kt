package com.pida.authentication.domain.auth.service

import com.pida.authentication.client.oauth.AppleClient
import com.pida.authentication.client.oauth.KaKaoClient
import com.pida.authentication.client.oauth.KaKaoClientResult
import com.pida.authentication.domain.auth.AppleClientResult
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import feign.FeignException
import org.springframework.stereotype.Service

@Service
class OAuthService(
    private val kaKaoClient: KaKaoClient,
    private val appleClient: AppleClient,
) {
    fun getKaKaoUserInfo(token: String): KaKaoClientResult =
        try {
            kaKaoClient.getUserInfo(token)
        } catch (e: FeignException) {
            if (e.status() == 401) {
                throw AuthenticationErrorException(AuthenticationErrorType.INVALID_KAKAO_TOKEN)
            } else {
                throw AuthenticationErrorException(AuthenticationErrorType.INVALID_KAKAO_TOKEN, e.message)
            }
        }

    fun getAppleUserInfo(token: String): AppleClientResult {
        if (!appleClient.verify(token)) throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
        return appleClient.getUserInfo(token)
    }
}
