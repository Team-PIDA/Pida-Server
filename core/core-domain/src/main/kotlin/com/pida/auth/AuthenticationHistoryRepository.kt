package com.pida.auth

interface AuthenticationHistoryRepository {
    fun create(newAuthenticationHistory: NewAuthenticationHistory): AuthenticationHistory

    fun findUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String?,
        refreshToken: String,
    ): AuthenticationHistory?

    fun update(updateAuthenticationHistory: UpdateAuthenticationHistory): AuthenticationHistory?

    fun removeToken(userKey: String): List<String>?

    fun findUserKey(userKey: String): AuthenticationHistory?

    fun remove(token: String): String
}
