package com.pida.presentation.v1.auth

import com.pida.auth.AuthenticationService
import com.pida.auth.CredentialSocial
import com.pida.auth.SocialType
import com.pida.client.oauth.OAuthService
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.auth.request.LoginRequest
import com.pida.presentation.v1.auth.request.SignUpRequest
import com.pida.presentation.v1.auth.request.TokenRequest
import com.pida.presentation.v1.auth.response.SignUpResponse
import com.pida.presentation.v1.auth.response.TokenResponse
import com.pida.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "1. Auth", description = "인증 관련 API")
@ApiV1Controller
class AuthController(
    private val authenticationService: AuthenticationService,
    private val oAuthService: OAuthService,
    private val userService: UserService,
) {
    @Operation(summary = "이메일 로그인", description = "로그인합니다.")
    @PostMapping("/auth/login")
    fun login(
        @RequestHeader(name = "X-DEVICE-ID") deviceId: String?,
        @RequestBody request: LoginRequest,
    ): TokenResponse {
        val token = authenticationService.login(deviceId, request.toCredentialsPida())
        return TokenResponse.toResponse(token)
    }

    @Operation(summary = "이메일 회원가입", description = "회원가입합니다.")
    @PostMapping("/auth/signup")
    suspend fun signUp(
        @RequestBody request: SignUpRequest,
    ): SignUpResponse {
        val newUser = userService.create(request.toNewUser())
        authenticationService.signUp(
            userId = newUser.id,
            userKey = newUser.key,
            newAuthenticationPida = request.toNewAuthenticationPida(),
        )
        return SignUpResponse("회원가입에 성공했습니다.")
    }

    @Operation(summary = "카카오 로그인", description = "카카오 소셜 로그인합니다.")
    @PostMapping("/auth/social-login/kakao")
    fun socialKakaoLogin(
        @RequestBody request: TokenRequest,
    ): TokenResponse {
        val socialInfo = oAuthService.getKaKaoUserInfo(request.token)
        val token =
            authenticationService.socialLogin(
                deviceId = "",
                credentialSocial =
                    CredentialSocial(
                        email = socialInfo.email,
                        socialId = socialInfo.id,
                        socialType = SocialType.KAKAO,
                    ),
            )
        return TokenResponse.toResponse(token)
    }

    @Operation(summary = "애플 소셜 로그인", description = "애플 소셜 로그인합니다.")
    @PostMapping("/auth/social-login/apple")
    fun socialAppleLogin(
        @RequestBody request: TokenRequest,
    ): TokenResponse {
        val socialInfo = oAuthService.getAppleUserInfo(request.token)
        val token =
            authenticationService.socialLogin(
                deviceId = "",
                credentialSocial =
                    CredentialSocial(
                        email = socialInfo.email,
                        socialId = socialInfo.id,
                        socialType = SocialType.APPLE,
                    ),
            )
        return TokenResponse.toResponse(token)
    }
}
