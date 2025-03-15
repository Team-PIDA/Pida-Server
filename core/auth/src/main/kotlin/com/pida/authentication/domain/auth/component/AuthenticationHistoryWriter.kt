package com.pida.authentication.domain.auth.component

import com.pida.authentication.domain.auth.NewAuthenticationHistory
import com.pida.authentication.domain.auth.repository.AuthenticationHistoryRepository
import org.springframework.stereotype.Component

@Component
class AuthenticationHistoryWriter(
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
) {
    fun write(newAuthenticationHistory: NewAuthenticationHistory) {
        authenticationHistoryRepository.create(newAuthenticationHistory)
    }
}
