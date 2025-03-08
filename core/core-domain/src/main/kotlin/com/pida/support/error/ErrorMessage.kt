package com.pida.support.error

data class ErrorMessage(
    val code: String,
    val message: String,
) {
    constructor(
        errorType: ErrorType,
        errorData: Any? = null,
    ) : this(
        code = errorType.name,
        message = (errorData?.toString() ?: errorType.message),
    )
}
