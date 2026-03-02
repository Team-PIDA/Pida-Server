package com.pida.notification.rain

import com.pida.notification.CreateNotificationStoredCommand
import com.pida.notification.EligibleUser
import com.pida.notification.FcmSender
import com.pida.notification.NewFirebaseCloudMessage
import com.pida.notification.NotificationService
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.support.extension.logger
import com.pida.user.device.UserDeviceReader
import org.springframework.stereotype.Service

/**
 * 내일 비 예보 푸시 알림 서비스
 */
@Service
class RainForecastNotificationService(
    private val rainForecastNotificationEligibilityChecker: RainForecastNotificationEligibilityChecker,
    private val rainForecastNotificationMessageBuilder: RainForecastNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * 내일 비 예보 알림 발송
     */
    fun sendRainForecastNotifications() {
        try {
            val eligibleUsers = rainForecastNotificationEligibilityChecker.findEligibleUsers()

            if (eligibleUsers.isEmpty()) {
                return
            }

            val pushTargets = resolvePushTargets(eligibleUsers)

            if (pushTargets.isEmpty()) {
                logger.info("No users with FCM token for rain forecast notification")
                return
            }

            val messages =
                pushTargets
                    .map { target ->
                        rainForecastNotificationMessageBuilder.buildMessage(target.fcmToken)
                    }.toSet()

            sendMessagesAndStoreRecords(messages, pushTargets.map { it.userId })

            logger.info("Rain forecast notification process completed successfully. sent=${messages.size}")
        } catch (e: Exception) {
            logger.error("Failed to send rain forecast notifications", e)
        }
    }

    private fun resolvePushTargets(users: List<EligibleUser>): List<PushTarget> =
        users
            .mapNotNull { user ->
                userDeviceReader.readLastByUserId(user.userId)?.let { device ->
                    PushTarget(
                        userId = user.userId,
                        fcmToken = device.fcmToken,
                    )
                }
            }.distinctBy { it.userId }

    private fun sendMessagesAndStoreRecords(
        messages: Set<NewFirebaseCloudMessage>,
        userIds: List<Long>,
    ) {
        if (messages.isEmpty() || userIds.isEmpty()) {
            return
        }

        fcmSender.sendAllAsync(messages)

        val messageContent = rainForecastNotificationMessageBuilder.getMessageContent()
        val commands =
            userIds.distinct().map { userId ->
                CreateNotificationStoredCommand(
                    notificationStoredKey = "",
                    userId = userId,
                    type = NotificationType.RAIN_FORECAST_ALERT,
                    parameterValue = "",
                    topic = "피다",
                    contents = messageContent,
                    readStatus = ReadStatus.UNREAD,
                )
            }

        notificationService.appendAll(commands)
    }

    private data class PushTarget(
        val userId: Long,
        val fcmToken: String,
    )
}
