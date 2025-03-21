package com.pida.presentation.v1.auth

import com.pida.authentication.domain.auth.service.AuthenticationService
import com.pida.presentation.v1.annotation.ApiV1Controller

@ApiV1Controller
class AuthController(
    private val authenticationService: AuthenticationService,
)
