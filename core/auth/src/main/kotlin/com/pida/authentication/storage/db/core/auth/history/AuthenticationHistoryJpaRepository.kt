package com.pida.authentication.storage.db.core.auth.history
import com.pida.authentication.storage.db.core.AuthenticationEntityStatus
import org.springframework.data.jpa.repository.JpaRepository

interface AuthenticationHistoryJpaRepository : JpaRepository<AuthenticationHistoryEntity, Long> {
    fun findAllByUserKeyAndDeviceId(
        userKey: String,
        deviceId: String,
    ): List<AuthenticationHistoryEntity>

    fun findByUserKeyAndEntityStatus(
        userKey: String,
        entityStatus: AuthenticationEntityStatus,
    ): AuthenticationHistoryEntity?

    fun findAllByUserKeyAndEntityStatus(
        userKey: String,
        status: AuthenticationEntityStatus,
    ): List<AuthenticationHistoryEntity>?

    fun findByAccessToken(accessToken: String): AuthenticationHistoryEntity?
}
