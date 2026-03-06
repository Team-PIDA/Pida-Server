package com.pida.notification.bloomed

import com.pida.notification.NewFirebaseCloudMessage
import com.pida.support.geo.Region
import org.springframework.stereotype.Component

/**
 * BLOOMED 알림 메시지 빌더
 *
 * 만개했어요 상태 알림의 FCM 메시지를 생성합니다.
 */
@Component
class BloomedNotificationMessageBuilder {
    companion object {
        private const val DESTINATION = "home"
    }

    /**
     * FCM 메시지 생성
     *
     * @param fcmToken FCM 토큰
     * @param region 지역
     * @return FCM 메시지
     */
    fun buildMessage(
        fcmToken: String,
        region: Region,
    ): NewFirebaseCloudMessage {
        val messageContent = getMessageContent(region)

        return NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = messageContent.lines()[0], // 첫 번째 줄을 제목으로
            body = messageContent.lines().drop(1).joinToString("\n"), // 나머지를 본문으로
            destination = DESTINATION,
        )
    }

    /**
     * 메시지 내용 반환 (NotificationStored 저장용)
     *
     * @param region 지역
     * @return 메시지 전체 내용
     */
    fun getMessageContent(region: Region): String = "${region.toKoreanName()}에도 벚꽃이 눈을 떴어요! 🌸\n우리 동네 가장 빠른 봄을 만나보세요."
}
