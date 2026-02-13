package com.pida.notification

sealed class NotificationStored {
    data class Create(
        val notificationStoredKey: String,
        val userId: Long,
        val type: NotificationType,
        val parameterValue: String,
        val topic: String,
        val contents: String,
        val readStatus: ReadStatus,
    )

    data class Info(
        val id: Long,
        val notificationStoredKey: String,
        val userId: Long,
        val type: NotificationType,
        val parameterValue: String,
        val topic: String,
        val contents: String,
        val readStatus: ReadStatus,
        val createdAt: String,
    )

    data class Result(
        val id: Long,
        val userId: Long,
        val nickname: String?,
        val spotName: String?,
        val site: String?,
        val type: String,
        val parameterValue: String,
        val topic: String,
        val contents: String,
        val readStatus: ReadStatus,
        val createdAt: String,
    )
}
