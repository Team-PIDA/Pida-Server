package com.pida.authentication.client.oauth

import com.pida.authentication.client.oauth.AppleClient.Companion.APPLE_URI
import com.pida.authentication.client.oauth.response.ApplePublicKeysResponse
import com.pida.authentication.client.oauth.response.AppleTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "apple-auth-api", url = APPLE_URI)
internal interface AppleApi {
    @GetMapping("/auth/keys")
    fun getApplePublicKeys(): ApplePublicKeysResponse

    @PostMapping("/auth/token")
    fun getAppleToken(
        headers: Map<String, String>,
        body: Map<String, String>,
    ): AppleTokenResponse
}
