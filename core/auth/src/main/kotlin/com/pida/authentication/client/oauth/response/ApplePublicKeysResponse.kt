package com.pida.authentication.client.oauth.response

data class ApplePublicKeysResponse(
    val keys: List<Key>,
)

data class Key(
    val kty: String,
    val kid: String,
    val use: String,
    val alg: String,
    val n: String,
    val e: String,
)
