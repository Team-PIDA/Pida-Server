package com.pida.notification.weekday

import com.pida.notification.NewFirebaseCloudMessage
import org.springframework.stereotype.Component

@Component
class WeekdayNotificationMessageBuilder {
    companion object {
        private const val MESSAGE_CONTENT =
            "오늘 하루도 고생했어요. \uD83C\uDF19 \n" +
                "퇴근길은 가까운 벚꽃 구경 어때요?"

        private const val DESTINATION = "home"
    }

    fun buildMessage(fcmToken: String): NewFirebaseCloudMessage =
        NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = MESSAGE_CONTENT.lines()[0], // 첫 번째 줄을 제목으로
            body = MESSAGE_CONTENT.lines().drop(1).joinToString("\n"), // 나머지를 본문으로
            destination = DESTINATION,
        )

    fun getMessageContent(): String = MESSAGE_CONTENT
}
