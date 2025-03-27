package com.pida.user

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userRepository: UserRepository,
) {
    companion object {
        /** 이메일 정규화 **/
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}\$")
    }

    suspend fun verifyEmail(email: String) {
        if (!email.matches(EMAIL_REGEX)) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_LOGIN_ID_FORMAT)
        }

        if (userRepository.existsByEmail(email)) {
            throw ErrorException(ErrorType.DUPLICATED_EMAIL)
        }
    }
}
