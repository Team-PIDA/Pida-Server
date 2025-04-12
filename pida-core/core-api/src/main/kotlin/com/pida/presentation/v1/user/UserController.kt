package com.pida.presentation.v1.user

import com.pida.auth.AuthenticationService
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.user.request.UpdateNicknameRequest
import com.pida.presentation.v1.user.response.UserProfileResponse
import com.pida.presentation.v1.user.response.UserWithdrawalResponse
import com.pida.user.NewUserWithdrawal
import com.pida.user.User
import com.pida.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "\uD83E\uDDCD\uD83C\uDFFB User API", description = "유저 관련 API")
@ApiV1Controller
class UserController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService,
) {
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @GetMapping("/users/me")
    suspend fun me(
        @Parameter(hidden = true, required = false) user: User,
    ): UserProfileResponse {
        val userProfile = userService.getProfile(user.id)
        return UserProfileResponse.of(userProfile)
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 진행합니다.")
    @DeleteMapping("/users")
    fun withdrawal(
        @Parameter(hidden = true, required = false) user: User,
    ): UserWithdrawalResponse {
        userService.deleteUser(
            NewUserWithdrawal(
                user = user,
            ),
        )
        authenticationService.delete(user.key)
        return UserWithdrawalResponse("회원탈퇴가 완료되었습니다.")
    }

    @Operation(summary = "닉네임 수정", description = "닉네임을 수정합니다.")
    @PutMapping("/users/nickname")
    suspend fun updateNickname(
        @Parameter(hidden = true, required = false) user: User,
        @RequestBody request: UpdateNicknameRequest,
    ): UserProfileResponse {
        val userProfile = userService.updateNickname(user.key, request.toUpdateNickname())
        return UserProfileResponse.of(userProfile)
    }
}
