package com.pida.storage.db.core.auth

import com.pida.auth.AuthenticationHistory
import com.pida.auth.AuthenticationHistoryRepository
import com.pida.auth.NewAuthenticationHistory
import com.pida.auth.UpdateAuthenticationHistory
import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.AuthenticationErrorType
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class AuthenticationHistoryCoreRepository(
    private val repository: AuthenticationHistoryJpaRepository,
    private val customRepository: AuthenticationHistoryCustomRepository,
) : AuthenticationHistoryRepository {
    @Transactional
    override fun create(newAuthenticationHistory: NewAuthenticationHistory): AuthenticationHistory {
        val saveHistory = repository.save(AuthenticationHistoryEntity(newAuthenticationHistory))
        return saveHistory.toAuthenticationHistory()
    }

    override fun findUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String?,
        refreshToken: String,
    ): AuthenticationHistory? {
        val histories =
            repository.findAllByUserKeyAndDeviceId(
                userKey = userKey,
                deviceId = deviceId,
            )
        return histories.find { it.refreshToken == refreshToken }?.toAuthenticationHistory()
    }

    override fun findUserKeyWithRefreshToken(
        userKey: String,
        refreshToken: String,
    ): AuthenticationHistory? =
        repository.findAllByUserKeyAndEntityStatus(userKey, AuthenticationEntityStatus.ACTIVE)?.find {
            it.refreshToken == refreshToken
        }?.toAuthenticationHistory()

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
            it.accessToken
        }

    override fun findUserKey(userKey: String): AuthenticationHistory? {
        val histories = repository.findAllByUserKeyAndEntityStatus(userKey, AuthenticationEntityStatus.ACTIVE)

        if (histories.isNullOrEmpty()) {
            return null
        }

        return histories.last().toAuthenticationHistory()
    }

    override fun findUserId(userId: Long): AuthenticationHistory? {
        val histories = repository.findAllByUserIdAndEntityStatus(userId, AuthenticationEntityStatus.ACTIVE)

        if (histories.isNullOrEmpty()) {
            return null
        }

        return histories.last().toAuthenticationHistory()
    }

    @Transactional
    override fun remove(token: String): String {
        val authenticationHistory =
            repository.findByAccessToken(token)
                ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_HISTORY)
        authenticationHistory.delete()
        return authenticationHistory.refreshToken
    }

    override fun findActiveUsersSince(sinceDate: LocalDateTime): List<Long> = customRepository.findActiveUserIdsSince(sinceDate)
}
