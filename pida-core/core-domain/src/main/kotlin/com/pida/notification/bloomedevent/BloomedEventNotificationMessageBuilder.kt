package com.pida.notification.bloomedevent

import com.pida.notification.NewFirebaseCloudMessage
import org.springframework.stereotype.Component

/**
 * 꽃 이벤트 만개 알림 메시지 빌더
 */
@Component
class BloomedEventNotificationMessageBuilder {
    companion object {
        private const val DESTINATION = "home"
    }

    fun buildMessage(
        fcmToken: String,
        eventName: String,
    ): NewFirebaseCloudMessage {
        val messageContent = getMessageContent(eventName)

        return NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = messageContent.lines().firstOrNull().orEmpty(),
            body = messageContent.lines().drop(1).joinToString("\n"),
            destination = DESTINATION,
        )
    }

    fun getMessageContent(eventName: String): String = "우리 동네에 만개한 꽃 이벤트가 열렸어요 🌸\n지금 $eventName 확인해보세요."
}
