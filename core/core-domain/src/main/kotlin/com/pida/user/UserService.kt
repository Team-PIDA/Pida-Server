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
        userValidator.verifyEmail(newUser.email)
        return userAppender.create(newUser)
    }

    suspend fun getProfile(userId: Long): UserProfile = userReader.readUserProfile(userId)

    fun getUser(userId: Long): User = userReader.readUser(userId)

    fun signInKakao(kakaoUserInfo: KakaoUserInfo): User {
        // 있으면 바로 return
        val user = userReader.readUserByEmail(kakaoUserInfo.email)
        return if (user != null) {
            user
        } else {
            val newUser =
                NewUser(
                    name = kakaoUserInfo.name,
                    email = kakaoUserInfo.email,
                    nickname = kakaoUserInfo.nickname,
                )
            userAppender.create(newUser)
        }
    }

    suspend fun updateNickname(
        userKey: String,
        updateNickname: UpdateNickname,
    ): UserProfile = userUpdater.updateNickname(userKey, updateNickname)

    suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile = userUpdater.updateEmail(userKey, email)

    suspend fun delete(userKey: String) {
        userDeleter.deleteUser(userKey)
    }

    suspend fun getAllUserProfile(userIds: List<Long>): List<UserProfile> = userReader.readAllByUserIds(userIds)

    suspend fun checkEmail(email: String) {
        userValidator.verifyEmail(email)
    }
}
