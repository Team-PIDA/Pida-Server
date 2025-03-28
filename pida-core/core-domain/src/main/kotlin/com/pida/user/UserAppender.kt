package com.pida.user

import org.springframework.stereotype.Component

@Component
class UserAppender(
    private val userRepository: UserRepository,
    private val userKeyGenerator: UserKeyGenerator,
) {
    fun create(newUser: NewUser): User {
        val newUserKey = userKeyGenerator.generate()
        return userRepository.create(
            newUser = newUser,
            newUserKey = newUserKey,
        )
    }
}
