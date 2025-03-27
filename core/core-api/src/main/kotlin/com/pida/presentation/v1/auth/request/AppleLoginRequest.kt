package com.pida.presentation.v1.auth.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "애플 로그인 요청 Json")
data class AppleLoginRequest(
    @Schema(description = "토큰", example = "id_token or authorizationCode")
    val token: String,
    @Schema(description = "사용자 이름", example = "김애플")
    val name: String,
)
