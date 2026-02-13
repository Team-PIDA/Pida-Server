package com.pida.storage.db.core.notification

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.pida.notification.ReadStatus
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationStoredJpaRepository :
    JpaRepository<NotificationStoredEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByUserIdAndReadStatus(
        userId: Long,
        readStatus: ReadStatus,
    ): List<NotificationStoredEntity>

    fun countByUserIdAndReadStatus(
        userId: Long,
        readStatus: ReadStatus?,
    ): Long
}
