package com.pida.support.error

data class AuthenticationErrorException(
    val authenticationErrorType: AuthenticationErrorType,
    val data: Any? = null,
) : RuntimeException(authenticationErrorType.message)
