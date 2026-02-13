package com.pida.notification

import com.pida.support.cursor.Cursor
import com.pida.support.cursor.CursorRequest
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationStoredAppender: NotificationStoredAppender,
    private val notificationStoredReader: NotificationStoredReader,
    private val notificationStoredUpdater: NotificationStoredUpdater,
    private val notificationStoredKeyGenerator: NotificationStoredKeyGenerator,
) {
    fun append(notificationStored: NotificationStored.Create): NotificationStored.Info =
        notificationStoredAppender.append(
            notificationStored.copy(
                notificationStoredKey = notificationStoredKeyGenerator.generate(),
            ),
        )

    fun appendAll(createAll: List<CreateNotificationStoredCommand>): List<NotificationStored.Info> =
        notificationStoredAppender.appendAll(
            createAll.map { notification ->
                NotificationStored.Create(
                    notificationStoredKey = notificationStoredKeyGenerator.generate(),
                    userId = notification.userId,
                    type = notification.type,
                    parameterValue = notification.parameterValue,
                    topic = notification.topic,
                    contents = notification.contents,
                    readStatus = ReadStatus.UNREAD,
                )
            },
        )

    fun findAllNotifications(
        userId: Long,
        cursorRequest: CursorRequest,
    ): Cursor<NotificationStored.Info> = notificationStoredReader.findAllBy(userId, cursorRequest)

    fun markAsRead(notificationId: Long) = notificationStoredUpdater.markAsRead(notificationId)
}
