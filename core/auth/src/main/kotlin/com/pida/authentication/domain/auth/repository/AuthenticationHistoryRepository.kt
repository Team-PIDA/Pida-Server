package com.pida.authentication.domain.auth.repository

import com.pida.authentication.domain.auth.AuthenticationHistory
import com.pida.authentication.domain.auth.NewAuthenticationHistory
import com.pida.authentication.domain.auth.UpdateAuthenticationHistory

interface AuthenticationHistoryRepository {
    fun create(newAuthenticationHistory: NewAuthenticationHistory): AuthenticationHistory

    fun findUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String,
        refreshToken: String,
    ): AuthenticationHistory?

    fun update(updateAuthenticationHistory: UpdateAuthenticationHistory): AuthenticationHistory?

    fun removeToken(userKey: String): List<String>?

    fun findUserKey(userKey: String): AuthenticationHistory

    fun remove(token: String): String
}
