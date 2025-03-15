package com.pida.authentication.domain.token.repository

import com.pida.authentication.domain.auth.Authentication
import com.pida.authentication.domain.auth.Provider
import com.pida.authentication.domain.token.PhoneToken
import com.pida.authentication.domain.token.Token

interface TokenRepository {
    fun create(
        deviceId: String,
        authentication: Authentication,
    ): Token

    fun renew(refreshToken: String): Token

    fun remove(token: String): String

    fun removeByUserKey(userKey: String)

    fun createPhoneJwt(phone: String): String

    fun getPhoneWithVerifyPhoneJwt(phoneToken: PhoneToken): String

    fun findBy(accessToken: String): Provider?
}
