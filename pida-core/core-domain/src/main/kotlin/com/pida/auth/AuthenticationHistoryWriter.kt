package com.pida.auth

import org.springframework.stereotype.Component

@Component
class AuthenticationHistoryWriter(
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
) {
    fun write(newAuthenticationHistory: NewAuthenticationHistory) {
        authenticationHistoryRepository.create(newAuthenticationHistory)
    }
}
