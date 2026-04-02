package com.pida.presentation.advice

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ApiExceptionAdviceTest {
    private val advice = ApiExceptionAdvice()

    @Test
    fun `authentication provider unavailable은 503으로 응답한다`() {
        val response =
            advice.handleAuthenticationErrorException(
                AuthenticationErrorException(AuthenticationErrorType.KAKAO_AUTH_PROVIDER_UNAVAILABLE),
            )

        response.statusCode.value() shouldBe 503
    }
}
