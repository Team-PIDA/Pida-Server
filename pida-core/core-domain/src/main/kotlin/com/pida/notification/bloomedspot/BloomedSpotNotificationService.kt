package com.pida.notification.bloomedspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotRepository
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
 * 벚꽃길 만개 이벤트 푸시 알림 서비스
 */
@Service
class BloomedSpotNotificationService(
    private val flowerSpotRepository: FlowerSpotRepository,
    private val bloomedSpotNotificationEligibilityChecker: BloomedSpotNotificationEligibilityChecker,
    private val bloomedSpotNotificationMessageBuilder: BloomedSpotNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * 특정 벚꽃길(spot)의 만개 이벤트 알림 발송
     */
    fun sendBloomedSpotNotification(flowerSpotId: Long) {
        try {
            val flowerSpot = readFlowerSpot(flowerSpotId) ?: return
            val eligibleUserIds = bloomedSpotNotificationEligibilityChecker.findEligibleUserIds(flowerSpot)

            if (eligibleUserIds.isEmpty()) {
                return
            }

            val targets =
                eligibleUserIds
                    .mapNotNull { userId ->
                        userDeviceReader.readLastByUserId(userId)?.let { device ->
                            PushTarget(
                                userId = userId,
                                fcmToken = device.fcmToken,
                            )
                        }
                    }.distinctBy { it.userId }

            if (targets.isEmpty()) {
                logger.info("No push target with valid FCM token for flowerSpotId=$flowerSpotId")
                return
            }

            val messages =
                targets
                    .map { target ->
                        bloomedSpotNotificationMessageBuilder.buildMessage(target.fcmToken, flowerSpot.streetName)
                    }.toSet()

            fcmSender.sendAllAsync(messages)

            val messageContent = bloomedSpotNotificationMessageBuilder.getMessageContent(flowerSpot.streetName)
            val commands =
                targets.map { target ->
                    CreateNotificationStoredCommand(
                        notificationStoredKey = "",
                        userId = target.userId,
                        type = NotificationType.BLOOMED_SPOT_ALERT,
                        parameterValue = flowerSpot.id.toString(),
                        topic = "피다",
                        contents = messageContent,
                        readStatus = ReadStatus.UNREAD,
                    )
                }

            notificationService.appendAll(commands)

            logger.info(
                "Bloomed spot notifications sent. flowerSpotId=$flowerSpotId, streetName=${flowerSpot.streetName}, sent=${messages.size}",
            )
        } catch (e: Exception) {
            logger.error("Failed to send bloomed spot notifications for flowerSpotId=$flowerSpotId", e)
        }
    }

    private fun readFlowerSpot(flowerSpotId: Long): FlowerSpot? =
        runCatching {
            runBlocking { flowerSpotRepository.findBy(flowerSpotId) }
        }.onFailure { error ->
            logger.error("Failed to read flower spot for bloomed notification. flowerSpotId=$flowerSpotId", error)
        }.getOrNull()

    private data class PushTarget(
        val userId: Long,
        val fcmToken: String,
    )
}
