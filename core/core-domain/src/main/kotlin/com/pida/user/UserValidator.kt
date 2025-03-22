package com.pida.user

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userReader: UserReader,
    private val userRepository: UserRepository,
) {
    suspend fun verify(newUser: NewUser) {
        if (userReader.existsByEmailOrPhone(newUser.email, newUser.phone)) {
            throw ErrorException(ErrorType.DUPLICATED_USER)
        }
    }

    suspend fun verifyPhone(phone: String) {
        if (userRepository.existsByPhone(phone)) {
            throw ErrorException(ErrorType.DUPLICATED_PHONE)
        }
    }

    suspend fun verifyEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorException(ErrorType.DUPLICATED_EMAIL)
        }
    }
}
