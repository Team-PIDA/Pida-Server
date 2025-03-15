package com.pida.authentication.storage.redis.auth

import com.pida.authentication.storage.redis.provider.ProviderDetail

data class TokenWithAuthentication(
    val accessToken: String,
    val refreshToken: String,
    val deviceId: String,
    val provider: ProviderDetail,
)
