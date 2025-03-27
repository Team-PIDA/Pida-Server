package com.pida.token.repository

import com.pida.auth.Provider
import com.pida.token.Token
import com.pida.user.SocialUser
import com.pida.user.User

interface TokenRepository {
    fun create(
        deviceId: String?,
        user: User,
    ): Token

    fun create(
        deviceId: String?,
        socialUser: SocialUser,
    ): Token

    fun renew(refreshToken: String): Token

    fun remove(token: String): String

    fun removeByUserKey(userKey: String)

    fun findBy(accessToken: String): Provider?
}
