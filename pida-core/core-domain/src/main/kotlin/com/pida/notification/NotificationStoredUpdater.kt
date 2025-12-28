package com.pida.notification

import org.springframework.stereotype.Component

@Component
class NotificationStoredUpdater(
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    fun markAsRead(notificationId: Long) = notificationStoredRepository.markAsRead(notificationId)
}
