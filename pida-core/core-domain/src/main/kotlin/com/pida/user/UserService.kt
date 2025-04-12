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
    fun create(newUser: NewUser): User {
        userValidator.verifyEmail(newUser.email)
        return userAppender.create(newUser)
    }

    suspend fun getProfile(userId: Long): UserProfile = userReader.readUserProfile(userId)

    fun getSocialUserByEmail(email: String): SocialUser? = userReader.readUserByEmail(email)

    fun getUser(userId: Long): User = userReader.readUser(userId)

    fun getUser(
        loginId: String,
        password: String,
    ): User = userReader.readUser(loginId, password)

    fun updateNickname(
        userKey: String,
        updateNickname: UpdateNickname,
    ): UserProfile {
        userValidator.verifyNickname(updateNickname.nickname)
        return userUpdater.updateNickname(userKey, updateNickname)
    }

    fun updateName(
        userKey: String,
        name: String,
    ): UserProfile {
        userValidator.verifyNickname(name)
        return userUpdater.updateName(userKey, name)
    }

    suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile = userUpdater.updateEmail(userKey, email)

    suspend fun getAllUserProfile(userIds: List<Long>): List<UserProfile> = userReader.readAllByUserIds(userIds)

    suspend fun checkEmail(email: String) {
        userValidator.verifyEmail(email)
    }

    fun deleteUser(newUserWithdrawal: NewUserWithdrawal) {
        userDeleter.deleteUser(newUserWithdrawal.user.key)
    }
}
