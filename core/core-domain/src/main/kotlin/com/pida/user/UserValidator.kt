package com.pida.user

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userRepository: UserRepository,
) {
    suspend fun verifyEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorException(ErrorType.DUPLICATED_EMAIL)
        }
    }
}
