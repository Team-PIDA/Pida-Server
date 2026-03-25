package com.pida.notification.bloomed

import com.pida.notification.NewFirebaseCloudMessage
import com.pida.support.geo.Region
import org.springframework.stereotype.Component

/**
 * BLOOMED 알림 메시지 빌더
 *
 * 개화 초기(첫 투표)와 만개 절정(80% 이상) 케이스에 따라 다른 메시지를 생성합니다.
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
     * @param alertType 알림 유형 (개화 초기 / 만개 절정)
     * @return FCM 메시지
     */
    fun buildMessage(
        fcmToken: String,
        region: Region,
        alertType: BloomedAlertType = BloomedAlertType.FIRST_VOTE,
    ): NewFirebaseCloudMessage {
        val messageContent = getMessageContent(region, alertType)

        return NewFirebaseCloudMessage(
            fcmToken = fcmToken,
            title = messageContent.lines()[0],
            body = messageContent.lines().drop(1).joinToString("\n"),
            destination = DESTINATION,
        )
    }

    /**
     * 메시지 내용 반환 (NotificationStored 저장용)
     *
     * @param region 지역
     * @param alertType 알림 유형 (개화 초기 / 만개 절정)
     * @return 메시지 전체 내용
     */
    fun getMessageContent(
        region: Region,
        alertType: BloomedAlertType = BloomedAlertType.FIRST_VOTE,
    ): String =
        when (alertType) {
            BloomedAlertType.FIRST_VOTE ->
                "${region.toKoreanName()}에도 벚꽃이 눈을 떴어요! 🌸\n우리 동네 가장 빠른 봄을 만나보세요."
            BloomedAlertType.THRESHOLD_REACHED ->
                "지금이 절정! 1년 중 가장 예쁜 ${region.toKoreanName()} 벚꽃의 만개 순간, 놓치면 후회해요. 🌸"
        }
}
