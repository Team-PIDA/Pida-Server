package com.pida.notification.withered

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
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * WITHERED 상태 푸시 알림 서비스
 *
 * 각 지역별 WITHERED 투표 비율을 확인하여 30% 이상인 지역의 사용자들에게 알림을 발송합니다.
 */
@Service
class WitheredNotificationService(
    private val witheredThresholdChecker: WitheredThresholdChecker,
    private val witheredNotificationEligibilityChecker: WitheredNotificationEligibilityChecker,
    private val witheredNotificationMessageBuilder: WitheredNotificationMessageBuilder,
    private val userDeviceReader: UserDeviceReader,
    private val fcmSender: FcmSender,
    private val notificationService: NotificationService,
) {
    private val logger by logger()

    /**
     * WITHERED 알림 발송
     *
     * 1. WITHERED 임계값을 초과한 지역 조회
     * 2. 각 지역별로 적격 사용자 조회
     * 3. FCM 메시지 생성 및 발송
     * 4. 알림 이력 저장
     */
    @Async
    fun sendWitheredNotifications() {
        try {
            // Step 1: WITHERED 비율이 30% 이상인 지역 조회
            val regionsExceedingThreshold = witheredThresholdChecker.findRegionsExceedingThreshold()

            if (regionsExceedingThreshold.isEmpty()) {
                return
            }

            // Step 2: 각 지역별로 알림 처리
            var totalSentCount = 0
            regionsExceedingThreshold.forEach { witheredRegion ->
                val sentCount = processRegion(witheredRegion.region)
                totalSentCount += sentCount
            }

            logger.info("Withered notification execution completed. Total $totalSentCount users notified.")
        } catch (e: Exception) {
            logger.error("Failed to send withered notifications", e)
        }
    }

    /**
     * 특정 지역에 대한 알림 처리
     *
     * @param region 대상 지역
     * @return 발송된 사용자 수
     */
    private fun processRegion(region: Region): Int {
        try {
            logger.info("Processing region: $region")

            // 1. 지역별 적격 사용자 조회
            val eligibleUsers = witheredNotificationEligibilityChecker.findEligibleUsersForRegion(region)

            if (eligibleUsers.isEmpty()) {
                logger.info("Region $region: No eligible users found")
                return 0
            }

            logger.info("Region $region: ${eligibleUsers.size} eligible users found")

            // 2. FCM 메시지 생성
            val messages = buildNotificationMessages(eligibleUsers)

            if (messages.isEmpty()) {
                logger.info("Region $region: No FCM messages to send (no valid FCM tokens)")
                return 0
            }

            logger.info("Region $region: ${messages.size} FCM messages prepared")

            // 3. FCM 발송
            fcmSender.sendAllAsync(messages)

            // 4. 알림 이력 저장
            storeNotificationRecords(eligibleUsers)

            logger.info("Region $region: Notifications sent successfully to ${messages.size} users")

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
     * @return FCM 메시지 집합
     */
    private fun buildNotificationMessages(users: List<EligibleUserWithRegion>): Set<NewFirebaseCloudMessage> =
        users
            .mapNotNull { user ->
                userDeviceReader.readLastByUserId(user.userId)?.let { device ->
                    witheredNotificationMessageBuilder.buildMessage(device.fcmToken)
                }
            }.toSet()

    /**
     * 알림 이력 저장
     *
     * @param users 대상 사용자 목록
     */
    private fun storeNotificationRecords(users: List<EligibleUserWithRegion>) {
        val commands =
            users.map { user ->
                CreateNotificationStoredCommand(
                    notificationStoredKey = "", // 서비스에서 재생성됨
                    userId = user.userId,
                    type = NotificationType.WITHERED_ALERT,
                    parameterValue = "",
                    topic = "피다",
                    contents = witheredNotificationMessageBuilder.getMessageContent(),
                    readStatus = ReadStatus.UNREAD,
                )
            }

        notificationService.appendAll(commands)
    }
}
