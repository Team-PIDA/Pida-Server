package com.pida.storage.db.core.notification

import com.pida.notification.NotificationStored
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "t_notification_stored")
class NotificationStoredEntity(
    val notificationStoredKey: String,
    val userId: Long,
    @Enumerated(value = EnumType.STRING)
    @Column(length = 30)
    val type: NotificationType,
    val parameterValue: String,
    val topic: String,
    val contents: String,
    @Enumerated(value = EnumType.STRING)
    @Column(length = 10)
    var readStatus: ReadStatus,
) : BaseEntity() {
    constructor(create: NotificationStored.Create) : this(
        notificationStoredKey = create.notificationStoredKey,
        userId = create.userId,
        type = create.type,
        parameterValue = create.parameterValue,
        topic = create.topic,
        contents = create.contents,
        readStatus = create.readStatus,
    )

    fun toNotificationStoredInfo(): NotificationStored.Info =
        NotificationStored.Info(
            id = id!!,
            notificationStoredKey = notificationStoredKey,
            userId = userId,
            type = type,
            parameterValue = parameterValue,
            topic = topic,
            contents = contents,
            readStatus = readStatus,
            createdAt = createdAt.toString(),
        )

    fun read() {
        this.readStatus = ReadStatus.READ
    }
}
