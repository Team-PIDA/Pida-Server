package com.pida.client.oauth

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import feign.FeignException
import feign.Request
import feign.Response
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OAuthServiceTest {
    private val kaKaoClient = mockk<KaKaoClient>()
    private val appleClient = mockk<AppleClient>()
    private val service = OAuthService(kaKaoClient, appleClient)

    @Test
    fun `카카오 401 응답은 INVALID_KAKAO_TOKEN으로 매핑한다`() {
        every { kaKaoClient.getUserInfo("token") } throws feignStatus(401)

        val exception = assertThrows<AuthenticationErrorException> { service.getKaKaoUserInfo("token") }

        exception.authenticationErrorType shouldBe AuthenticationErrorType.INVALID_KAKAO_TOKEN
    }

    @Test
    fun `카카오 upstream 장애는 provider unavailable로 매핑한다`() {
        every { kaKaoClient.getUserInfo("token") } throws IllegalStateException("kakao down")

        val exception = assertThrows<AuthenticationErrorException> { service.getKaKaoUserInfo("token") }

        exception.authenticationErrorType shouldBe AuthenticationErrorType.KAKAO_AUTH_PROVIDER_UNAVAILABLE
        exception.authenticationErrorType.status shouldBe 503
    }

    private fun feignStatus(status: Int): FeignException =
        FeignException.errorStatus(
            "getKaKaoUserInfo",
            Response
                .builder()
                .status(status)
                .reason("upstream")
                .request(
                    Request.create(
                        Request.HttpMethod.GET,
                        "https://kapi.kakao.com/v2/user/me",
                        emptyMap(),
                        null,
                        Charsets.UTF_8,
                        null,
                    ),
                ).headers(emptyMap())
                .build(),
        )
}
