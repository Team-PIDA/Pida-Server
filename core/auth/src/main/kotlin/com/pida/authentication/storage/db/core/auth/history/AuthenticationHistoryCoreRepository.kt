package com.pida.authentication.storage.db.core.auth.history

import com.pida.authentication.domain.auth.AuthenticationHistory
import com.pida.authentication.domain.auth.NewAuthenticationHistory
import com.pida.authentication.domain.auth.UpdateAuthenticationHistory
import com.pida.authentication.domain.auth.repository.AuthenticationHistoryRepository
import com.pida.authentication.storage.db.core.AuthenticationEntityStatus
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class AuthenticationHistoryCoreRepository(
    private val repository: AuthenticationHistoryJpaRepository,
) : AuthenticationHistoryRepository {
    override fun create(newAuthenticationHistory: NewAuthenticationHistory): AuthenticationHistory {
        val saveHistory = repository.save(AuthenticationHistoryEntity(newAuthenticationHistory))
        return saveHistory.toAuthenticationHistory()
    }

    override fun findUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String,
        refreshToken: String,
    ): AuthenticationHistory? {
        val histories =
            repository.findAllByUserKeyAndDeviceId(
                userKey = userKey,
                deviceId = deviceId,
            )
        return histories.find { it.refreshToken == refreshToken }?.toAuthenticationHistory()
    }

    @Transactional
    override fun update(updateAuthenticationHistory: UpdateAuthenticationHistory): AuthenticationHistory? {
        val histories =
            repository.findAllByUserKeyAndDeviceId(
                userKey = updateAuthenticationHistory.userKey,
                deviceId = updateAuthenticationHistory.deviceId,
            )
        return histories
            .find {
                it.refreshToken == updateAuthenticationHistory.refreshToken
            }?.updateRefreshToken(updateAuthenticationHistory.newToken.token)
    }

    @Transactional
    override fun removeToken(userKey: String): List<String>? =
        repository.findAllByUserKeyAndEntityStatus(userKey, AuthenticationEntityStatus.ACTIVE)?.map {
            it.delete()
            it.refreshToken
        }

    override fun findUserKey(userKey: String): AuthenticationHistory {
        val authenticationHistory =
            repository.findAllByUserKeyAndEntityStatus(userKey, AuthenticationEntityStatus.ACTIVE)
                ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)
        return authenticationHistory.last().toAuthenticationHistory()
    }

    @Transactional
    override fun remove(token: String): String {
        val authenticationHistory =
            repository.findByAccessToken(token)
                ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)
        authenticationHistory.delete()
        return authenticationHistory.refreshToken
    }
}
