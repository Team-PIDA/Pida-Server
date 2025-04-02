package com.pida.storage.db.core.auth
import org.springframework.data.jpa.repository.JpaRepository

interface AuthenticationHistoryJpaRepository : JpaRepository<AuthenticationHistoryEntity, Long> {
    fun findAllByUserKeyAndDeviceId(
        userKey: String,
        deviceId: String?,
    ): List<AuthenticationHistoryEntity>

    fun findAllByUserKeyAndEntityStatus(
        userKey: String,
        status: AuthenticationEntityStatus,
    ): List<AuthenticationHistoryEntity>?

    fun findAllByUserIdAndEntityStatus(
        userId: Long,
        active: AuthenticationEntityStatus,
    ): List<AuthenticationHistoryEntity>?

    fun findByAccessToken(accessToken: String): AuthenticationHistoryEntity?
}
