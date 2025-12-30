package com.pida.notification

import org.springframework.stereotype.Component

@Component
class NotificationStoredAppender(
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    fun append(create: NotificationStored.Create): NotificationStored.Info = notificationStoredRepository.save(create)

    fun appendAll(createAll: List<NotificationStored.Create>) = notificationStoredRepository.saveAll(createAll)
}
