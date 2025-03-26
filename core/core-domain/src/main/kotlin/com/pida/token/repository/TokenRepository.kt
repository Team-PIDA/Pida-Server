package com.pida.token.repository

import com.pida.auth.Provider
import com.pida.token.Token

interface TokenRepository {
    fun create(
        deviceId: String?,
        authentication: Authentication,
    ): Token

    fun renew(refreshToken: String): Token

    fun remove(token: String): String

    fun removeByUserKey(userKey: String)

    fun findBy(accessToken: String): Provider?
}
