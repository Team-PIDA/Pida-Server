package com.pida.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val userAppender: UserAppender,
    private val userReader: UserReader,
    private val userUpdater: UserUpdater,
    private val userDeleter: UserDeleter,
    private val userValidator: UserValidator,
) {
    suspend fun create(newUser: NewUser): User {
        userValidator.verify(newUser)
        return userAppender.create(newUser)
    }

    suspend fun getProfile(userId: Long): UserProfile = userReader.readUserProfile(userId)

    suspend fun getUser(userId: Long): User = userReader.readUser(userId)

    suspend fun updateNickname(
        userKey: String,
        updateNickname: UpdateNickname,
    ): UserProfile = userUpdater.updateNickname(userKey, updateNickname)

    suspend fun updatePhone(
        userKey: String,
        phone: String,
    ): UserProfile {
        userValidator.verifyPhone(phone)
        return userUpdater.updatePhone(userKey, phone)
    }

    suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile = userUpdater.updateEmail(userKey, email)

    suspend fun delete(userKey: String) {
        userDeleter.deleteUser(userKey)
    }

    suspend fun getUser(
        name: String,
        phone: String,
    ): UserProfile = userReader.readUser(name, phone)

    suspend fun getUserProfile(
        name: String,
        phone: String,
    ): UserProfile {
        val userProfile = userReader.readUserProfile(name, phone)
        return userProfile
    }

    suspend fun getAllUserProfile(userIds: List<Long>): List<UserProfile> = userReader.readAllByUserIds(userIds)

    suspend fun checkEmail(email: String) {
        userValidator.verifyEmail(email)
    }

    suspend fun verifyUser(validateNewUser: ValidateNewUser) {
        userValidator.verifyPhone(validateNewUser.phone)
    }
}
