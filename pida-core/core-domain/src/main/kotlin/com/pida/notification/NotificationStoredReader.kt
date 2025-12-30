package com.pida.notification

import com.pida.support.cursor.Cursor
import com.pida.support.cursor.CursorRequest
import org.springframework.stereotype.Component

@Component
class NotificationStoredReader(
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    fun findAllBy(
        userId: Long,
        cursorRequest: CursorRequest,
    ): Cursor<NotificationStored.Info> = notificationStoredRepository.findAllBy(userId, cursorRequest)
}
