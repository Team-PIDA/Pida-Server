package com.pida.support.error

/**
 * ErrorType Enum
 *
 * @property kind [ErrorKind] 에러 종류
 * @property message [String] message
 * @property level [ErrorLevel] 에러 로그 레벨
 */
enum class ErrorType(
    val status: Int,
    val kind: ErrorKind,
    val message: String,
    val level: ErrorLevel,
) {
    /** Common */
    DEFAULT(500, ErrorKind.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다.", ErrorLevel.ERROR),
    NOT_FOUND_DATA(404, ErrorKind.NOT_FOUND, "해당 데이터를 찾지 못했습니다.", ErrorLevel.INFO),
    INVALID_REQUEST(400, ErrorKind.CLIENT_ERROR, "잘못된 요청입니다.", ErrorLevel.WARN),
    INVALID_SIMULTANEOUS_REQUEST(400, ErrorKind.CLIENT_ERROR, "동시에 요청할 수 없습니다.", ErrorLevel.ERROR),
    METHOD_ARGUMENT_TYPE_MISMATCH(400, ErrorKind.CLIENT_ERROR, "요청 한 값 타입이 잘못되어 binding에 실패하였습니다.", ErrorLevel.ERROR),
    METHOD_NOT_ALLOWED(400, ErrorKind.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP method 입니다.", ErrorLevel.ERROR),
    INTERNAL_SERVER_ERROR(500, ErrorKind.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요", ErrorLevel.ERROR),

    /** Authorization */
    UNAUTHORIZED_TOKEN(401, ErrorKind.AUTHORIZATION, "인증정보가 필요한 요청입니다.", ErrorLevel.WARN),
    INVALID_TOKEN(401, ErrorKind.AUTHORIZATION, "유효하지 않은 토큰입니다.", ErrorLevel.WARN),
    INVALID_ACCESS_TOKEN(401, ErrorKind.AUTHORIZATION, "잘못된 accessToken 입니다.", ErrorLevel.WARN),
    INVALID_REFRESH_TOKEN(401, ErrorKind.AUTHORIZATION, "잘못된 refreshToken 입니다.", ErrorLevel.WARN),

    /** Sign */
    INVALID_CREDENTIALS(400, ErrorKind.CLIENT_ERROR, "아이디 혹은 비밀번호가 올바르지 않습니다.", ErrorLevel.WARN),
    WITHDRAWAL_USER(403, ErrorKind.FORBIDDEN_ERROR, "사용할 수 없는 계정입니다.", ErrorLevel.WARN),
    DUPLICATED_USER(409, ErrorKind.CLIENT_ERROR, "이미 존재하는 계정입니다.", ErrorLevel.INFO),
    DUPLICATED_PHONE(409, ErrorKind.CLIENT_ERROR, "이미 존재하는 휴대폰 번호입니다.", ErrorLevel.INFO),
    INVALID_LOGIN_ID_FORMAT(400, ErrorKind.CLIENT_ERROR, "이메일 형식이 올바르지 않습니다.", ErrorLevel.INFO),
    INVALID_PASSWORD_FORMAT(
        400,
        ErrorKind.CLIENT_ERROR,
        "비밀번호 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.",
        ErrorLevel.INFO,
    ),
    INVALID_NAME_FORMAT(400, ErrorKind.CLIENT_ERROR, "이름을 제대로 입력해 주세요.", ErrorLevel.INFO),
    INVALID_NICKNAME_FORMAT(400, ErrorKind.CLIENT_ERROR, "닉네임은 6자 이하로 입력해 주세요.", ErrorLevel.INFO),

    /** Member */
    NOT_FOUND_USER(404, ErrorKind.SERVER_ERROR, "사용자가 존재하지 않습니다.", ErrorLevel.WARN),
    INVALID_PASSWORD(400, ErrorKind.CLIENT_ERROR, "기존 비밀번호가 올바르지 않습니다.", ErrorLevel.WARN),
    INVALID_NEW_PASSWORD(400, ErrorKind.CLIENT_ERROR, "새 비밀번호가 기존 비밀번호와 같을 수 없습니다.", ErrorLevel.WARN),
    INVALID_NEW_LOGIN_ID(400, ErrorKind.CLIENT_ERROR, "새로 입력한 아이디와 기존 아이디는 같을 수 없습니다.", ErrorLevel.WARN),
}
