package com.pida.user

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class UserReader(
    private val userRepository: UserRepository,
) {
    suspend fun readUserProfile(userId: Long): UserProfile = userRepository.readByUserId(userId)

    suspend fun readUserProfileOrNull(userId: Long): UserProfile? = userRepository.readByUserIdOrNull(userId)

    suspend fun readUserProfile(userKey: String): UserProfile = userRepository.readByUserKey(userKey)

    fun readUser(userId: Long): User {
        val user = userRepository.readUserById(userId) ?: throw ErrorException(ErrorType.NOT_FOUND_USER)
        return user
    }

    fun readUser(
        loginId: String,
        password: String,
    ): User = userRepository.readUser(loginId, password)

    suspend fun readAllByUserIds(userIds: List<Long>): List<UserProfile> = userRepository.readAllByUserIds(userIds)

    fun readUserByEmail(email: String): SocialUser? = userRepository.readUserByEmail(email)
}
