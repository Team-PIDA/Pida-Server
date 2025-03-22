package com.pida.presentation.v1.auth.request

import com.pida.authentication.domain.auth.CredentialsPida

data class LoginRequest(
    val loginId: String,
    val password: String,
) {
    fun toCredentialsPida(): CredentialsPida =
        CredentialsPida(
            loginId = loginId,
            password = password,
        )
}
