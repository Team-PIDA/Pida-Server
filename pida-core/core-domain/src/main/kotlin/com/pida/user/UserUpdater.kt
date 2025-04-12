package com.pida.user

import org.springframework.stereotype.Component

@Component
class UserUpdater(
    private val userRepository: UserRepository,
) {
    fun updateNickname(
        userKey: String,
        updateNickname: UpdateNickname,
    ): UserProfile = userRepository.updateNickname(userKey, updateNickname.nickname)

    fun updateName(
        userKey: String,
        name: String,
    ): UserProfile = userRepository.updateName(userKey, name)

    suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile = userRepository.updateEmail(userKey, email)
}
