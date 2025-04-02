package com.pida.auth

interface RedisTokenRepository {
    fun create(
        accessToken: String,
        refreshToken: String,
        deviceId: String?,
        providerDetail: ProviderDetail,
        accessTokenExpiration: Long,
        refreshTokenExpiration: Long,
    ): TokenWithAuthentication

    fun findByToken(token: String): TokenWithAuthentication

    fun findBy(accessToken: String): Provider?

    fun deleteToken(token: String)

    fun deleteAllToken(token: String)
}
