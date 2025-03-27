package com.pida.presentation.v1.user

import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.user.response.UserProfileResponse
import com.pida.user.User
import com.pida.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "2. User", description = "유저 관련 API")
@ApiV1Controller
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @GetMapping("/users/me")
    suspend fun me(user: User): UserProfileResponse {
        val userProfile = userService.getProfile(user.id)
        return UserProfileResponse.of(userProfile)
    }
}
