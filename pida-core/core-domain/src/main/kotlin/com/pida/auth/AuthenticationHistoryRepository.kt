package com.pida.auth

import java.time.LocalDateTime

interface AuthenticationHistoryRepository {
    fun create(newAuthenticationHistory: NewAuthenticationHistory): AuthenticationHistory

    fun findUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String?,
        refreshToken: String,
    ): AuthenticationHistory?

    fun findUserKeyWithRefreshToken(
        userKey: String,
        refreshToken: String,
    ): AuthenticationHistory?

    fun update(updateAuthenticationHistory: UpdateAuthenticationHistory): AuthenticationHistory?

    fun findUserKey(userKey: String): AuthenticationHistory?

    fun findUserId(userId: Long): AuthenticationHistory?

    fun removeToken(userKey: String): List<String>?

    fun remove(token: String): String

    fun findActiveUsersSince(sinceDate: LocalDateTime): List<Long>
}
