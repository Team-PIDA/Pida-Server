package com.pida.notification.bloomed

import com.pida.notification.CreateNotificationStoredCommand
import com.pida.notification.EligibleUserWithRegion
import com.pida.notification.FcmSender
import com.pida.notification.NewFirebaseCloudMessage
import com.pida.notification.NotificationService
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.support.extension.logger
import com.pida.support.geo.Region
import com.pida.user.device.UserDeviceReader
import org.springframework.stereotype.Service

/**
 * BLOOMED 상태 푸시 알림 서비스
 *
 * 특정 지역에 대해 BLOOMED 알림을 발송합니다.
 */
@Service
class BloomedNotificationService(
    private val bloomedThresholdChecker: BloomedThresholdChecker,
    private val bloomedNotificationEligibilityChecker: BloomedNotificationEligibilityChecker,
    private val bloomedNotificationMessageBuilder: BloomedNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * BLOOMED 비율이 80% 이상인 지역들에 대해 알림을 발송합니다.
     */
    fun sendBloomedNotifications() {
        try {
            val regionsExceedingThreshold = bloomedThresholdChecker.findRegionsExceedingThreshold()

            if (regionsExceedingThreshold.isEmpty()) {
                return
            }

            var totalSentCount = 0
            regionsExceedingThreshold.forEach { bloomedRegion ->
                val sentCount = sendBloomedNotificationForRegion(bloomedRegion.region)
                totalSentCount += sentCount
            }

            logger.info("Bloomed threshold notification execution completed. Total $totalSentCount users notified.")
        } catch (e: Exception) {
            logger.error("Failed to send bloomed threshold notifications", e)
        }
    }

    /**
     * 특정 지역에 대해 BLOOMED 알림을 발송합니다.
     */
    fun sendBloomedNotificationForRegion(region: Region): Int {
        try {
            logger.info("Processing region: $region")

            // 1. 지역별 적격 사용자 조회
            val eligibleUsers = bloomedNotificationEligibilityChecker.findEligibleUsersForRegion(region)

            if (eligibleUsers.isEmpty()) {
                logger.info("Region $region: No eligible users found")
                return 0
            }

            // 2. FCM 메시지 생성
            val messages = buildNotificationMessages(eligibleUsers, region)

            if (messages.isEmpty()) {
                return 0
            }
            // 3. FCM 발송
            fcmSender.sendAllAsync(messages)

            // 4. 알림 이력 저장
            storeNotificationRecords(eligibleUsers, region)

            return messages.size
        } catch (e: Exception) {
            logger.error("Failed to process region: $region", e)
            return 0
        }
    }

    /**
     * FCM 메시지 빌드
     *
     * @param users 대상 사용자 목록
     * @param region 대상 지역
     * @return FCM 메시지 집합
     */
    private fun buildNotificationMessages(
        users: List<EligibleUserWithRegion>,
        region: Region,
    ): Set<NewFirebaseCloudMessage> =
        userDeviceReader.readLastByUserIds(users.map { it.userId }).let { latestDevicesByUserId ->
            users
                .mapNotNull { user ->
                    latestDevicesByUserId[user.userId]?.let { device ->
                        bloomedNotificationMessageBuilder.buildMessage(device.fcmToken, region)
                    }
                }.toSet()
        }

    /**
     * 알림 이력 저장
     *
     * @param users 대상 사용자 목록
     * @param region 대상 지역
     */
    private fun storeNotificationRecords(
        users: List<EligibleUserWithRegion>,
        region: Region,
    ) {
        val commands =
            users.map { user ->
                CreateNotificationStoredCommand(
                    notificationStoredKey = "", // 서비스에서 재생성됨
                    userId = user.userId,
                    type = NotificationType.BLOOMED_ALERT,
                    parameterValue = region.name,
                    topic = "피다",
                    contents = bloomedNotificationMessageBuilder.getMessageContent(region),
                    readStatus = ReadStatus.UNREAD,
                )
            }

        notificationService.appendAll(commands)
    }
}
