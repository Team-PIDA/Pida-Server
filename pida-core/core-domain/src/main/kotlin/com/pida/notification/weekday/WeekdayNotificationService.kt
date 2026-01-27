package com.pida.notification.weekday

import com.pida.notification.CreateNotificationStoredCommand
import com.pida.notification.FcmSender
import com.pida.notification.NewFirebaseCloudMessage
import com.pida.notification.NotificationService
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.notification.weekend.EligibleUser
import com.pida.support.extension.logger
import com.pida.user.device.UserDeviceReader
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * 평일 힐링 푸시 알림 서비스
 */
@Service
class WeekdayNotificationService(
    private val weekdayNotificationEligibilityChecker: WeekdayNotificationEligibilityChecker,
    private val weekdayNotificationMessageBuilder: WeekdayNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * 평일 알림 발송
     *
     * 1. 대상 사용자 조회
     * 2. FCM 메시지 생성 및 발송
     * 3. 알림 이력 저장
     */
    @Async
    fun sendWeekdayNotifications() {
        try {
            logger.info("Starting weekday notification process")

            // Step 1: 대상 사용자 조회
            val eligibleUsers = weekdayNotificationEligibilityChecker.findEligibleUsers()

            if (eligibleUsers.isEmpty()) {
                logger.info("No eligible users found for weekday notification")
                return
            }

            logger.info("Found ${eligibleUsers.size} eligible users for weekday notification")

            // Step 2: FCM 메시지 생성
            val messages = buildNotificationMessages(eligibleUsers)

            if (messages.isEmpty()) {
                logger.warn("No FCM tokens found for eligible users")
                return
            }

            logger.info("Sending ${messages.size} FCM messages")

            // Step 3: FCM 발송
            fcmSender.sendAllAsync(messages) { results ->
                val successCount = results.count { it.sent }
                val failCount = results.count { !it.sent }

                logger.info("Weekday notification FCM send completed: $successCount succeeded, $failCount failed")
            }

            // Step 4: 알림 이력 저장
            storeNotificationRecords(eligibleUsers)

            logger.info("Weekday notification process completed successfully")
        } catch (e: Exception) {
            logger.error("Failed to send weekday notifications", e)
        }
    }

    /**
     * FCM 메시지 빌드
     *
     * @param users 대상 사용자 목록
     * @return FCM 메시지 집합
     */
    private fun buildNotificationMessages(users: List<EligibleUser>): Set<NewFirebaseCloudMessage> =
        users
            .mapNotNull { user ->
                userDeviceReader.readLastByUserId(user.userId)?.let { device ->
                    weekdayNotificationMessageBuilder.buildMessage(device.fcmToken)
                }
            }.toSet()

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
                    type = NotificationType.WEEKDAY_HEALING,
                    parameterValue = "",
                    topic = "weekday_healing",
                    contents = weekdayNotificationMessageBuilder.getMessageContent(),
                    readStatus = ReadStatus.UNREAD,
                )
            }

        notificationService.appendAll(commands)

        logger.info("Stored ${commands.size} notification records")
    }
}
