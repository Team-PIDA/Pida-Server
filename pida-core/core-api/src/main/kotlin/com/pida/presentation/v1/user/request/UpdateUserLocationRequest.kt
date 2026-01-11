package com.pida.presentation.v1.user.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "유저 위치 정보 수정 요청 Json")
data class UpdateUserLocationRequest(
    @Schema(description = "위도", example = "37.123456")
    val latitude: Double,
    @Schema(description = "경도", example = "127.123456")
    val longitude: Double,
)
