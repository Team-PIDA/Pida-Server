package com.pida.presentation.v1.user.response

import com.pida.user.UserProfile
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "유저 프로필 응답 Json")
data class UserProfileResponse(
    @Schema(
        description = "유저 아이디",
        example = "1",
    )
    val userId: Long,
    @Schema(description = "이메일", example = "test@test.com")
    val email: String,
    @Schema(description = "이름", example = "차윤범")
    val name: String,
    @Schema(description = "닉네임", example = "윤범초이")
    val nickname: String,
) {
    companion object {
        fun of(userProfile: UserProfile): UserProfileResponse =
            UserProfileResponse(
                userId = userProfile.id,
                email = userProfile.email,
                name = userProfile.name,
                nickname = userProfile.nickname,
            )
    }
}
