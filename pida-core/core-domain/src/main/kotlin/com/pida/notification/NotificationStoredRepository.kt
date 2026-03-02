package com.pida.notification

import com.pida.support.cursor.Cursor
import com.pida.support.cursor.CursorRequest
import java.time.LocalDateTime

interface NotificationStoredRepository {
    fun save(notificationStored: NotificationStored.Create): NotificationStored.Info

    fun saveAll(createAll: List<NotificationStored.Create>): List<NotificationStored.Info>

    fun findById(notificationStoredId: Long): NotificationStored.Info

    fun findByUserIdAndReadStatus(
        userId: Long,
        readStatus: ReadStatus,
    ): List<NotificationStored.Info>

    fun findAllBy(
        userId: Long,
        cursorRequest: CursorRequest,
    ): Cursor<NotificationStored.Info>

    fun countByUserIdAndReadStatus(
        userId: Long,
        readStatus: ReadStatus?,
    ): Long

    fun countByUserIdsAndTypeAndCreatedAtAfter(
        userIds: List<Long>,
        type: NotificationType,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long>

    fun countByUserIdsAndTypeNotAndCreatedAtAfter(
        userIds: List<Long>,
        excludedType: NotificationType,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long>

    fun markAsRead(notificationId: Long)
}
