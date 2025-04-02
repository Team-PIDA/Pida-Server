package com.pida.user

import org.springframework.stereotype.Component

@Component
class UserDeleter(
    private val userRepository: UserRepository,
) {
    suspend fun deleteUser(userKey: String) {
        userRepository.delete(userKey)
    }
}
