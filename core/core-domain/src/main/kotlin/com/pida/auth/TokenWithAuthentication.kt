package com.pida.auth

import com.pida.storage.redis.provider.ProviderDetail

data class TokenWithAuthentication(
    val accessToken: String,
    val refreshToken: String,
    val deviceId: String?,
    val provider: ProviderDetail,
)
