package com.pida.presentation.v1.auth.request

import com.pida.auth.AuthorityType
import com.pida.auth.GrantedAuthority
import com.pida.auth.NewAuthenticationPida
import com.pida.user.NewUser
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청 Json")
data class SignUpRequest(
    @Schema(description = "이메일", example = "test@test.com")
    val email: String,
    @Schema(description = "비밀번호", example = "password")
    val password: String,
    @Schema(description = "이름", example = "윤범차")
    val name: String,
    @Schema(description = "이름", example = "닉네임이야")
    val nickname: String,
) {
    fun toNewUser(): NewUser =
        NewUser(
            email = email,
            name = name,
            nickname = nickname,
        )

    fun toNewAuthenticationPida(): NewAuthenticationPida =
        NewAuthenticationPida(
            loginId = email,
            password = password,
            grantedAuthority = GrantedAuthority(AuthorityType.USER),
        )
}
