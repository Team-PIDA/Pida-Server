package com.pida.storage.db.core.user.device

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserDeviceJpaRepository :
    JpaRepository<UserDeviceEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findAllByDeletedAtIsNull(): List<UserDeviceEntity>

    fun findFirstByUserIdAndDeletedAtIsNullOrderByCreatedAtDescIdDesc(userId: Long): UserDeviceEntity?

    @Query(
        """
        SELECT DISTINCT ON (user_id) *
        FROM t_user_device
        WHERE deleted_at IS NULL
          AND user_id IN (:userIds)
        ORDER BY user_id, created_at DESC, id DESC
        """,
        nativeQuery = true,
    )
    fun findLastByUserIds(
        @Param("userIds") userIds: List<Long>,
    ): List<UserDeviceEntity>

    fun findAllByUserKeyAndDeletedAtIsNull(userKey: String): List<UserDeviceEntity>

    fun findAllByUserIdAndDeletedAtIsNull(userId: Long): List<UserDeviceEntity>
}
