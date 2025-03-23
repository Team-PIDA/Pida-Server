package com.pida.authentication.client.oauth.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AppleTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("id_token")
    val idToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
) {
    companion object {
        fun of(
            accessToken: String,
            expiresIn: Long,
            idToken: String,
            refreshToken: String,
            tokenType: String,
        ) = AppleTokenResponse(accessToken, expiresIn, idToken, refreshToken, tokenType)
    }
}
