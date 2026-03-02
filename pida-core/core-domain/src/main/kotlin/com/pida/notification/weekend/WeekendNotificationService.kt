package com.pida.notification.weekend

import com.pida.notification.CreateNotificationStoredCommand
import com.pida.notification.EligibleUser
import com.pida.notification.FcmSender
import com.pida.notification.NewFirebaseCloudMessage
import com.pida.notification.NotificationService
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.support.extension.logger
import com.pida.user.device.UserDeviceReader
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * 주말 힐링 푸시 알림 서비스
 */
@Service
class WeekendNotificationService(
    private val weekendNotificationEligibilityChecker: WeekendNotificationEligibilityChecker,
    private val weekendNotificationMessageBuilder: WeekendNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * 주말 알림 발송
     *
     * 1. 대상 사용자 조회
     * 2. FCM 메시지 생성 및 발송
     * 3. 알림 이력 저장
     */
    @Async
    fun sendWeekendNotifications() {
        try {
            // Step 1: 대상 사용자 조회
            val eligibleUsers = weekendNotificationEligibilityChecker.findEligibleUsers()

            if (eligibleUsers.isEmpty()) {
                return
            }

            // Step 2: FCM 메시지 생성
            val messages = buildNotificationMessages(eligibleUsers)

            if (messages.isEmpty()) {
                return
            }

            // Step 3: FCM 발송
            fcmSender.sendAllAsync(messages)

            // Step 4: 알림 이력 저장
            storeNotificationRecords(eligibleUsers)

            logger.info("Weekend notification process completed successfully")
        } catch (e: Exception) {
            logger.error("Failed to send weekend notifications", e)
        }
    }

    /**
     * FCM 메시지 빌드
     *
     * @param users 대상 사용자 목록
     * @return FCM 메시지 집합
     */
    private fun buildNotificationMessages(users: List<EligibleUser>): Set<NewFirebaseCloudMessage> =
        userDeviceReader.readLastByUserIds(users.map { it.userId }).let { latestDevicesByUserId ->
            users
                .mapNotNull { user ->
                    latestDevicesByUserId[user.userId]?.let { device ->
                        weekendNotificationMessageBuilder.buildMessage(device.fcmToken)
                    }
                }.toSet()
        }

    /**
     * 알림 이력 저장
     *
     * @param users 대상 사용자 목록
     */
    private fun storeNotificationRecords(users: List<EligibleUser>) {
        val commands =
            users.map { user ->
                CreateNotificationStoredCommand(
                    notificationStoredKey = "", // 서비스에서 재생성됨
                    userId = user.userId,
                    type = NotificationType.WEEKEND_HEALING,
                    parameterValue = "",
                    topic = "피다",
                    contents = weekendNotificationMessageBuilder.getMessageContent(),
                    readStatus = ReadStatus.UNREAD,
                )
            }

        notificationService.appendAll(commands)
    }
}
