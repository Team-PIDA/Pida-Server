package com.pida.notification.withered

import com.pida.notification.NewFirebaseCloudMessage
import org.springframework.stereotype.Component

/**
 * WITHERED 알림 메시지 빌더
 *
 * 저물었어요 상태 알림의 FCM 메시지를 생성합니다.
 */
@Component
class WitheredNotificationMessageBuilder {
    companion object {
        private const val MESSAGE_CONTENT =
            "이번 주말이 지나면 늦을 지도 몰라요.  🌸\n엔딩 크레딧 올라가기 전, 마지막 벚꽃 산책 어때요?"

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
