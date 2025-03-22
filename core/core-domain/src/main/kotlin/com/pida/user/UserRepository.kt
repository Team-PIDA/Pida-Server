package com.pida.user

interface UserRepository {
    // Create
    fun create(
        newUser: NewUser,
        newUserKey: NewUserKey,
    ): User

    // Read
    suspend fun readByUserId(id: Long): UserProfile

    suspend fun readByUserKey(userKey: String): UserProfile

    suspend fun readUserById(id: Long): User?

    suspend fun readByNameAndPhone(
        name: String,
        phone: String,
    ): UserProfile

    suspend fun readByUserIdOrNull(id: Long): UserProfile?

    suspend fun readByPhoneNumber(phoneNumber: String): UserProfile?

    suspend fun readAllByUserIds(userIds: List<Long>): List<UserProfile>

    suspend fun existsByEmail(email: String): Boolean

    suspend fun existsByPhone(phone: String): Boolean

    suspend fun existsByEmailOrPhone(
        email: String,
        phone: String,
    ): Boolean

    // Update
    suspend fun updateNickname(
        userKey: String,
        nickname: String,
    ): UserProfile

    suspend fun updatePhone(
        userKey: String,
        phone: String,
    ): UserProfile

    suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile

    // Delete
    suspend fun delete(userKey: String)
}
