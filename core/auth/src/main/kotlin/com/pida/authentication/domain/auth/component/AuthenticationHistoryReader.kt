package com.pida.authentication.domain.auth.component

import com.pida.authentication.domain.auth.AuthenticationHistory
import com.pida.authentication.domain.auth.repository.AuthenticationHistoryRepository
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import org.springframework.stereotype.Component

@Component
class AuthenticationHistoryReader(
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
) {
    fun readByUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String,
        refreshToken: String,
    ): AuthenticationHistory =
        authenticationHistoryRepository.findUserKeyWithDeviceWithRefreshToken(userKey, deviceId, refreshToken)
            ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)

    fun readByUserKey(userKey: String): AuthenticationHistory = authenticationHistoryRepository.findUserKey(userKey)
}
