package com.pida.client.oauth

import com.pida.client.oauth.response.ApplePublicKeysResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "apple-auth-api", url = "https://appleid.apple.com")
internal interface AppleApi {
    @GetMapping("/auth/keys")
    fun getApplePublicKeys(): ApplePublicKeysResponse

    @PostMapping("/auth/revoke", consumes = ["application/x-www-form-urlencoded"])
    fun revokeToken(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("token") token: String,
        @RequestParam("token_type_hint") tokenTypeHint: String = "refresh_token",
    )
}
