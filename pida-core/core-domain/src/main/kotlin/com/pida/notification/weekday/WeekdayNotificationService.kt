package com.pida.notification.weekday

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
    fun sendWeekdayNotifications() = sendWeekdayNotificationsSync()

    /**
     * 평일 알림 동기 실행
     *
     * evening 오케스트레이터에서 실행 순서를 보장하기 위해 사용됩니다.
     */
    fun sendWeekdayNotificationsSync() {
        try {
            // Step 1: 대상 사용자 조회
            val eligibleUsers = weekdayNotificationEligibilityChecker.findEligibleUsers()

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

            logger.info("Weekday notification process completed successfully")
        } catch (e: Exception) {
            logger.error("Failed to send weekday notifications", e)
        }
    }

    private fun buildNotificationMessages(users: List<EligibleUser>): Set<NewFirebaseCloudMessage> =
        users
            .mapNotNull { user ->
                userDeviceReader.readLastByUserId(user.userId)?.let { device ->
                    weekdayNotificationMessageBuilder.buildMessage(device.fcmToken)
                }
            }.toSet()

    private fun storeNotificationRecords(users: List<EligibleUser>) {
        val commands =
            users.map { user ->
                CreateNotificationStoredCommand(
                    notificationStoredKey = "", // 서비스에서 재생성됨
                    userId = user.userId,
                    type = NotificationType.WEEKDAY_HEALING,
                    parameterValue = "",
                    topic = "피다",
                    contents = weekdayNotificationMessageBuilder.getMessageContent(),
                    readStatus = ReadStatus.UNREAD,
                )
            }

        notificationService.appendAll(commands)

        logger.info("Stored ${commands.size} notification records")
    }
}
