package com.pida.auth

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import org.springframework.stereotype.Component

@Component
class AuthenticationHistoryUpdater(
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
) {
    fun update(updateAuthenticationHistory: UpdateAuthenticationHistory): AuthenticationHistory =
        authenticationHistoryRepository.update(updateAuthenticationHistory)
            ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)

    fun removeToken(userKey: String): List<String> =
        authenticationHistoryRepository.removeToken(userKey)
            ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)

    fun remove(token: String): String = authenticationHistoryRepository.remove(token)
}
