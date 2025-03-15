package com.pida.authentication.client.oauth

import org.springframework.stereotype.Repository

@Repository
class AppleApiCaller(
    private val appleClient: AppleClient,
)
