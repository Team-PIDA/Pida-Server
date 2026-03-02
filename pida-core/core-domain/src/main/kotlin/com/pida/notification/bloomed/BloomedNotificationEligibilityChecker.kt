package com.pida.notification.bloomed

import com.pida.notification.EligibleUserWithRegion
import com.pida.notification.NotificationStoredRepository
import com.pida.notification.NotificationType
import com.pida.support.extension.logger
import com.pida.support.geo.Region
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * BLOOMED 알림 적격성 검증 컴포넌트
 *
 * 특정 지역의 사용자 중 알림을 받을 수 있는 사용자를 필터링합니다.
 */
@Component
class BloomedNotificationEligibilityChecker(
    private val userReader: BloomedNotificationUserReader,
    private val regionResolver: BloomedRegionResolver,
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    private val logger by logger()

    /**
     * 특정 지역의 사용자 중 알림 적격 사용자 조회
     *
     * 조건:
     * - 최근 7일 내 활성 사용자
     * - 위치 정보가 있는 사용자
     * - 현재 위치가 대상 지역에 속한 사용자
     * - 올해 BLOOMED_ALERT 알림을 받지 않은 사용자
     *
     * @param region 대상 지역
     * @return 적격 사용자 목록
     */
    fun findEligibleUsersForRegion(region: Region): List<EligibleUserWithRegion> {
        // 1. 활성 사용자 조회
        val activeUsers = userReader.findActiveUsersWithLocation()

        if (activeUsers.isEmpty()) {
            logger.info("No active users found")
            return emptyList()
        }

        logger.info("Found ${activeUsers.size} active users with location")

        // 2. 각 사용자의 Region 해석 및 필터링
        val usersInTargetRegion =
            activeUsers.mapNotNull { user ->
                val userRegion = regionResolver.resolveRegion(user.latitude, user.longitude)

                if (userRegion == region) {
                    EligibleUserWithRegion(
                        userId = user.userId,
                        latitude = user.latitude,
                        longitude = user.longitude,
                        region = userRegion,
                    )
                } else {
                    null
                }
            }

        logger.info("${usersInTargetRegion.size} users are in target region: $region")

        if (usersInTargetRegion.isEmpty()) {
            return emptyList()
        }

        // 3. 올해 BLOOMED_ALERT 알림 수신 여부 확인
        val yearStartDateTime = getYearStartDateTime()
        val userIds = usersInTargetRegion.map { it.userId }
        val notificationCountMap =
            notificationStoredRepository.countByUserIdsAndTypeAndCreatedAtAfter(
                userIds = userIds,
                type = NotificationType.BLOOMED_ALERT,
                createdAtAfter = yearStartDateTime,
            )

        logger.info("Yearly notification counts retrieved for ${notificationCountMap.size} users")

        // 4. 필터링: 올해 알림을 받지 않은 사용자만
        val eligibleUsers =
            usersInTargetRegion.filter { user ->
                val count = notificationCountMap[user.userId] ?: 0
                count == 0L
            }

        logger.info("${eligibleUsers.size} users have not received BLOOMED_ALERT this year")

        return eligibleUsers
    }

    /**
     * 올해 1월 1일 00:00:00 반환
     */
    private fun getYearStartDateTime() =
        LocalDate
            .now()
            .withDayOfYear(1)
            .atStartOfDay()
}
