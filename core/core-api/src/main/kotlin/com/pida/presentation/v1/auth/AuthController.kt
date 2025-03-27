package com.pida.presentation.v1.auth

import com.pida.auth.AuthenticationFacade
import com.pida.auth.AuthenticationService
import com.pida.auth.CredentialSocial
import com.pida.auth.SocialType
import com.pida.client.oauth.OAuthService
import com.pida.presentation.v1.annotation.ApiV1Controller
import com.pida.presentation.v1.auth.request.AppleLoginRequest
import com.pida.presentation.v1.auth.request.LoginRequest
import com.pida.presentation.v1.auth.request.SignUpRequest
import com.pida.presentation.v1.auth.request.SignUpSocialRequest
import com.pida.presentation.v1.auth.request.TokenRequest
import com.pida.presentation.v1.auth.response.SignUpResponse
import com.pida.presentation.v1.auth.response.TokenResponse
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.user.UpdateNickname
import com.pida.user.User
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
    private val authenticationFacade: AuthenticationFacade,
    private val oAuthService: OAuthService,
    private val userService: UserService,
) {
    @Operation(summary = "이메일 로그인", description = "로그인합니다.")
    @PostMapping("/auth/login")
    fun login(
        @RequestHeader(name = "X-DEVICE-ID") deviceId: String?,
        @RequestBody request: LoginRequest,
    ): TokenResponse {
        // TODO: 이메일 로그인 도입 시 password Encoder 적용 및 validation 추가
        val findUser = userService.getUser(request.loginId, request.password)
        val token = authenticationService.login(deviceId, User(findUser.id, findUser.key), request.toCredentialsPida())
        return TokenResponse.toResponse(false, token)
    }

    @Operation(summary = "이메일 회원가입", description = "회원가입합니다.")
    @PostMapping("/auth/signup")
    suspend fun signUp(
        @RequestBody request: SignUpRequest,
    ): SignUpResponse {
        userService.create(request.toNewUser())
        return SignUpResponse("회원가입에 성공했습니다.")
    }

    @Operation(summary = "카카오 로그인", description = "카카오 소셜 로그인합니다.")
    @PostMapping("/auth/social-login/kakao")
    suspend fun socialKakaoLogin(
        @RequestBody request: TokenRequest,
    ): TokenResponse {
        val socialInfo = oAuthService.getKaKaoUserInfo(request.token)
        val (isTemporaryToken, token) =
            authenticationFacade.socialLogin(
                deviceId = "",
                credentialSocial =
                    CredentialSocial(
                        email = socialInfo.email,
                        name = socialInfo.name,
                        socialId = socialInfo.id,
                        socialType = SocialType.KAKAO,
                    ),
            )
        return TokenResponse.toResponse(isTemporaryToken, token)
    }

    @Operation(summary = "애플 소셜 로그인", description = "애플 소셜 로그인합니다.")
    @PostMapping("/auth/social-login/apple")
    suspend fun socialAppleLogin(
        @RequestBody request: AppleLoginRequest,
    ): TokenResponse {
        val socialInfo = oAuthService.getAppleUserInfo(request.token)
        val (isTemporaryToken, token) =
            authenticationFacade.socialLogin(
                deviceId = "",
                credentialSocial =
                    CredentialSocial(
                        email = socialInfo.email,
                        name = request.name,
                        socialId = socialInfo.id,
                        socialType = SocialType.APPLE,
                    ),
            )
        return TokenResponse.toResponse(isTemporaryToken, token)
    }

    @Operation(summary = "소셜 회원가입", description = "소셜 회원 가입합니다.")
    @PostMapping("/auth/social-signup")
    suspend fun socialSignUp(
        @RequestBody request: SignUpSocialRequest,
    ): SignUpResponse {
        val tempUser = userService.getSocialUserByEmail(request.email)
        if (tempUser == null) {
            throw ErrorException(ErrorType.NOT_FOUND_DATA)
        } else {
            userService.updateNickname(tempUser.key, UpdateNickname(request.name))
        }
        return SignUpResponse("회원가입에 성공했습니다.")
    }
}
