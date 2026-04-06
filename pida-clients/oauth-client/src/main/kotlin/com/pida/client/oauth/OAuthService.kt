package com.pida.client.oauth

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
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
            if (e.status() in setOf(400, 401, 403)) {
                throw AuthenticationErrorException(AuthenticationErrorType.INVALID_KAKAO_TOKEN)
            } else {
                throw AuthenticationErrorException(AuthenticationErrorType.KAKAO_AUTH_PROVIDER_UNAVAILABLE, e.message)
            }
        } catch (e: Exception) {
            throw AuthenticationErrorException(AuthenticationErrorType.KAKAO_AUTH_PROVIDER_UNAVAILABLE, e.message)
        }

    fun getAppleUserInfo(token: String): AppleClientResult {
        if (!appleClient.verify(token)) throw AuthenticationErrorException(AuthenticationErrorType.INVALID_APPLE_TOKEN)
        return appleClient.getUserInfo(token)
    }
}
