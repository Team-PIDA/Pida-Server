package com.pida.notification.bloomedspot

import com.pida.notification.NewFirebaseCloudMessage
import org.springframework.stereotype.Component

/**
 * 벚꽃길 만개 이벤트 알림 메시지 빌더
 */
@Component
class BloomedSpotNotificationMessageBuilder {
    companion object {
        private const val DESTINATION = "home"
    }

    fun buildMessage(
        fcmToken: String,
        streetName: String,
    ): NewFirebaseCloudMessage {
        val messageContent = getMessageContent(streetName)

        return NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = messageContent.lines().firstOrNull().orEmpty(),
            body = messageContent.lines().drop(1).joinToString("\n"),
            destination = DESTINATION,
        )
    }

    fun getMessageContent(streetName: String): String = "우리 동네에 만개한 벚꽃길이 생겼어요 🌸\n지금 제일 예쁜 $streetName 확인해보세요."
}
