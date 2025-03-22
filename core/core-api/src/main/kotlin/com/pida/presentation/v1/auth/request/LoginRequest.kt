package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.auth.CredentialsPida
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청")
data class LoginRequest(
    @Schema(description = "로그인 아이디", example = "test@test.com")
    val loginId: String,
    @Schema(description = "비밀번호", example = "password")
    val password: String,
) {
    fun toCredentialsPida(): CredentialsPida =
        CredentialsPida(
            loginId = loginId,
            password = password,
        )
}
