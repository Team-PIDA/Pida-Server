package com.pida.notification.rain

import com.pida.notification.NewFirebaseCloudMessage
import org.springframework.stereotype.Component

/**
 * 비 예보 알림 메시지 빌더
 */
@Component
class RainForecastNotificationMessageBuilder {
    companion object {
        private const val DESTINATION = "home"
        private const val TITLE = "내일 비 소식이 있어요 ☔️"
        private const val BODY = "마지막 꽃구경 찬스! 오늘 밤 산책을 놓치지 마세요."
    }

    fun buildMessage(fcmToken: String): NewFirebaseCloudMessage =
        NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = TITLE,
            body = BODY,
            destination = DESTINATION,
        )

    fun getMessageContent(): String = "$TITLE\n$BODY"
}
