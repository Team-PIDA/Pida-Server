package com.pida.presentation.v1.auth.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 응답 Json")
data class SignUpResponse(
    @Schema(description = "회원가입 메시지", example = "회원가입에 성공했습니다.")
    val message: String,
)
