package com.pida.authentication.support.error

enum class AuthenticationErrorType(
    val kind: AuthenticationErrorKind,
    val message: String,
    val level: AuthenticationErrorLevel,
) {
    /** Common */
    DEFAULT(AuthenticationErrorKind.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다.", AuthenticationErrorLevel.ERROR),
    NOT_FOUND_DATA(AuthenticationErrorKind.SERVER_ERROR, "해당 데이터를 찾지 못했습니다.", AuthenticationErrorLevel.INFO),
    INVALID_REQUEST(AuthenticationErrorKind.CLIENT_ERROR, "잘못된 요청입니다.", AuthenticationErrorLevel.WARN),

    /** Authorization */
    INVALID_KAKAO(AuthenticationErrorKind.CLIENT_ERROR, "카카오 요청에 실패하였습니다.", AuthenticationErrorLevel.INFO),
    NOT_FOUND_HISTORY(AuthenticationErrorKind.SERVER_ERROR, "히스토리 정보가 존재하지 않습니다.", AuthenticationErrorLevel.INFO),
    UNAUTHORIZED_TOKEN(AuthenticationErrorKind.AUTHORIZATION, "인증정보가 필요한 요청입니다.", AuthenticationErrorLevel.WARN),
    INVALID_TOKEN(AuthenticationErrorKind.AUTHORIZATION, "유효하지 않은 토큰입니다.", AuthenticationErrorLevel.WARN),
    INVALID_KAKAO_TOKEN(AuthenticationErrorKind.AUTHORIZATION, "유효하지 않은 카카오 토큰입니다.", AuthenticationErrorLevel.ERROR),
    INVALID_APPLE_TOKEN(AuthenticationErrorKind.AUTHORIZATION, "유효하지 않은 애플 토큰입니다.", AuthenticationErrorLevel.ERROR),
    INVALID_ACCESS_TOKEN(AuthenticationErrorKind.AUTHORIZATION, "유효하지 accessToken 입니다.", AuthenticationErrorLevel.WARN),
    INVALID_REFRESH_TOKEN(
        AuthenticationErrorKind.AUTHORIZATION,
        "잘못된 refreshToken 입니다.",
        AuthenticationErrorLevel.WARN,
    ),

    /** Sign */
    INVALID_CREDENTIALS(AuthenticationErrorKind.SERVER_ERROR, "아이디 혹은 비밀번호가 올바르지 않습니다.", AuthenticationErrorLevel.WARN),
    DUPLICATED_USER(AuthenticationErrorKind.CLIENT_ERROR, "이미 존재하는 계정입니다.", AuthenticationErrorLevel.INFO),
    DUPLICATED_PHONE(AuthenticationErrorKind.CLIENT_ERROR, "이미 존재하는 휴대폰 번호입니다.", AuthenticationErrorLevel.INFO),

    WITHDRAWAL_USER(AuthenticationErrorKind.AUTHORIZATION, "사용할 수 없는 계정입니다.", AuthenticationErrorLevel.WARN),
    INVALID_LOGIN_ID_FORMAT(AuthenticationErrorKind.CLIENT_ERROR, "이메일 형식이 올바르지 않습니다.", AuthenticationErrorLevel.INFO),

    /** User */
    INVALID_PASSWORD(AuthenticationErrorKind.CLIENT_ERROR, "기존 비밀번호가 올바르지 않습니다.", AuthenticationErrorLevel.WARN),
    INVALID_NEW_PASSWORD(
        AuthenticationErrorKind.CLIENT_ERROR,
        "새 비밀번호가 기존 비밀번호와 같을 수 없습니다.",
        AuthenticationErrorLevel.WARN,
    ),
    INVALID_NEW_LOGIN_ID(
        AuthenticationErrorKind.CLIENT_ERROR,
        "새로 입력한 아이디와 기존 아이디는 같을 수 없습니다.",
        AuthenticationErrorLevel.WARN,
    ),
}
