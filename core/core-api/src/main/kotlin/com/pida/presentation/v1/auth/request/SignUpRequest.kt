package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.auth.AuthorityType
import com.pida.authentication.domain.auth.GrantedAuthority
import com.pida.authentication.domain.auth.NewAuthenticationPida
import com.pida.user.Gender
import com.pida.user.NewUser
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

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
    @Schema(description = "전화번호", example = "01012345678")
    val phone: String,
    @Schema(description = "성별", example = "ETC")
    val gender: Gender,
    @Schema(description = "생년월일", example = "1996-11-25")
    val birth: LocalDate,
) {
    fun toNewUser(): NewUser =
        NewUser(
            email = email,
            name = name,
            nickname = nickname,
            phone = phone,
            gender = gender,
            birth = birth,
        )

    fun toNewAuthenticationPida(): NewAuthenticationPida =
        NewAuthenticationPida(
            loginId = email,
            password = password,
            grantedAuthority = GrantedAuthority(AuthorityType.USER),
        )
}
