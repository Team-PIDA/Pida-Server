package com.pida.notification.bloomedevent

import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventRepository
import com.pida.notification.CreateNotificationStoredCommand
import com.pida.notification.FcmSender
import com.pida.notification.NotificationService
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.support.extension.logger
import com.pida.user.device.UserDeviceReader
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

/**
 * 꽃 이벤트 만개 이벤트 푸시 알림 서비스
 */
@Service
class BloomedEventNotificationService(
    private val flowerEventRepository: FlowerEventRepository,
    private val bloomedEventNotificationEligibilityChecker: BloomedEventNotificationEligibilityChecker,
    private val bloomedEventNotificationMessageBuilder: BloomedEventNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * 특정 꽃 이벤트의 만개 이벤트 알림 발송
     */
    fun sendBloomedEventNotification(flowerEventId: Long) {
        try {
            val flowerEvent = readFlowerEvent(flowerEventId) ?: return
            val eligibleUserIds = bloomedEventNotificationEligibilityChecker.findEligibleUserIds(flowerEvent)

            if (eligibleUserIds.isEmpty()) {
                return
            }

            val latestDevicesByUserId = userDeviceReader.readLastByUserIds(eligibleUserIds)
            val targets =
                eligibleUserIds
                    .mapNotNull { userId ->
                        latestDevicesByUserId[userId]?.let { device ->
                            PushTarget(
                                userId = userId,
                                fcmToken = device.fcmToken,
                            )
                        }
                    }.distinctBy { it.userId }

            if (targets.isEmpty()) {
                logger.info("No push target with valid FCM token for flowerEventId=$flowerEventId")
                return
            }

            val messages =
                targets
                    .map { target ->
                        bloomedEventNotificationMessageBuilder.buildMessage(target.fcmToken, flowerEvent.name)
                    }.toSet()

            fcmSender.sendAllAsync(messages)

            val messageContent = bloomedEventNotificationMessageBuilder.getMessageContent(flowerEvent.name)
            val commands =
                targets.map { target ->
                    CreateNotificationStoredCommand(
                        notificationStoredKey = "",
                        userId = target.userId,
                        type = NotificationType.BLOOMED_EVENT_ALERT,
                        parameterValue = flowerEvent.id.toString(),
                        topic = "피다",
                        contents = messageContent,
                        readStatus = ReadStatus.UNREAD,
                    )
                }

            notificationService.appendAll(commands)

            logger.info(
                "Bloomed event notifications sent. flowerEventId=$flowerEventId, eventName=${flowerEvent.name}, sent=${messages.size}",
            )
        } catch (e: Exception) {
            logger.error("Failed to send bloomed event notifications for flowerEventId=$flowerEventId", e)
        }
    }

    private fun readFlowerEvent(flowerEventId: Long): FlowerEvent? =
        runCatching {
            runBlocking { flowerEventRepository.findBy(flowerEventId) }
        }.onFailure { error ->
            logger.error("Failed to read flower event for bloomed notification. flowerEventId=$flowerEventId", error)
        }.getOrNull()

    private data class PushTarget(
        val userId: Long,
        val fcmToken: String,
    )
}
