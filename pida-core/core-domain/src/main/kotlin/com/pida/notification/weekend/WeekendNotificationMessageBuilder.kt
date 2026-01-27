package com.pida.notification.weekend

import com.pida.notification.NewFirebaseCloudMessage
import org.springframework.stereotype.Component

/**
 * 주말 알림 메시지 빌더
 */
@Component
class WeekendNotificationMessageBuilder {
    companion object {
        private const val MESSAGE_CONTENT =
            "이번 주도 치열하게 보낸 당신에게 🎁 \n걷기만 해도 힐링되는 벚꽃길이 기다려요."

        private const val DESTINATION = "home"
    }

    /**
     * FCM 메시지 생성
     *
     * @param fcmToken FCM 토큰
     * @return FCM 메시지
     */
    fun buildMessage(fcmToken: String): NewFirebaseCloudMessage =
        NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = MESSAGE_CONTENT.lines()[0], // 첫 번째 줄을 제목으로
            body = MESSAGE_CONTENT.lines().drop(1).joinToString("\n"), // 나머지를 본문으로
            destination = DESTINATION,
        )

    /**
     * 메시지 내용 반환 (NotificationStored 저장용)
     *
     * @return 메시지 전체 내용
     */
    fun getMessageContent(): String = MESSAGE_CONTENT
}
