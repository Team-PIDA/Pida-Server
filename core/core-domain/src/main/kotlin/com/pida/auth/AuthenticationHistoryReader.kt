package com.pida.auth

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import org.springframework.stereotype.Component

@Component
class AuthenticationHistoryReader(
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
) {
    fun readByUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String?,
        refreshToken: String,
    ): AuthenticationHistory =
        authenticationHistoryRepository.findUserKeyWithDeviceWithRefreshToken(userKey, deviceId, refreshToken)
            ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)

    fun readByUserKey(userKey: String): AuthenticationHistory? = authenticationHistoryRepository.findUserKey(userKey)
}
