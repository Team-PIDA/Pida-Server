package com.pida.presentation.v1.user.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "FCM 토큰 등록 응답 Json")
data class UserMobileDeviceResponse(
    @Schema(description = "FCM 토큰 등록 메시지", example = "FCM 토큰이 등록되었습니다.")
    val message: String,
)
