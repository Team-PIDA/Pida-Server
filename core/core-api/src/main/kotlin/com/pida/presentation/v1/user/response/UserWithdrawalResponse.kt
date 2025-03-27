package com.pida.presentation.v1.user.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원탈퇴 응답")
data class UserWithdrawalResponse(
    @Schema(description = "메시지", example = "회원탈퇴가 완료되었습니다.")
    val message: String,
)
