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

    fun readUserById(id: Long): User?

    suspend fun readByUserIdOrNull(id: Long): UserProfile?

    suspend fun readAllByUserIds(userIds: List<Long>): List<UserProfile>

    suspend fun existsByEmail(email: String): Boolean

    fun readUserByEmail(email: String): User?

    // Update
    suspend fun updateNickname(
        userKey: String,
        nickname: String,
    ): UserProfile

    suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile

    // Delete
    suspend fun delete(userKey: String)
}
