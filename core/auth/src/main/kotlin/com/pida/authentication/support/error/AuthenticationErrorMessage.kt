package com.pida.authentication.support.error

data class AuthenticationErrorMessage(
    val code: String,
    val message: String,
) {
    constructor(
        authenticationErrorType: AuthenticationErrorType,
        errorData: Any? = null,
    ) : this(
        code = authenticationErrorType.name,
        message = authenticationErrorType.message + (errorData?.toString() ?: ""),
    )
}
