package com.pida.notification.weekday

import com.pida.notification.EligibleUser
import com.pida.notification.NotificationStoredRepository
import com.pida.notification.NotificationType
import com.pida.notification.weekend.WeekendNotificationAirQualityChecker
import com.pida.notification.weekend.WeekendNotificationLocationChecker
import com.pida.notification.weekend.WeekendNotificationUserReader
import com.pida.support.extension.logger
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

/**
 * 평일 알림 적격성 검증 컴포넌트
 */
@Component
class WeekdayNotificationEligibilityChecker(
    private val userReader: WeekendNotificationUserReader,
    private val locationChecker: WeekendNotificationLocationChecker,
    private val airQualityChecker: WeekendNotificationAirQualityChecker,
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    private val logger by logger()

    companion object {
        private const val MAX_NOTIFICATIONS_PER_WEEK = 2
    }

    /**
     * 평일 알림을 받을 수 있는 사용자 조회
     *
     * 조건:
     * - 최근 30일 내 활성 사용자
     * - 위치 정보가 있는 사용자
     * - 3km 반경 내 개화 상태(BLOOMED) FlowerSpot 존재
     * - PM10 < 81µg/m³
     * - 이번 주 평일 알림 수신 횟수 2회 미만
     *
     * @return 적격 사용자 목록
     */
    fun findEligibleUsers(): List<EligibleUser> {
        // 1. 활성 사용자 조회
        val activeUsers = userReader.findActiveUsersWithLocation()

        if (activeUsers.isEmpty()) {
            logger.info("No active users found")
            return emptyList()
        }

        logger.info("Found ${activeUsers.size} active users with location")

        // 2. 이번 주 평일 알림 수신 횟수 조회
        val weekStartDateTime = getWeekStartDateTime()
        val userIds = activeUsers.map { it.userId }
        val notificationCountMap =
            notificationStoredRepository.countByUserIdsAndTypeAndCreatedAtAfter(
                userIds = userIds,
                type = NotificationType.WEEKDAY_HEALING,
                createdAtAfter = weekStartDateTime,
            )

        logger.info("Weekly notification counts retrieved for ${notificationCountMap.size} users")

        // 3. 필터링: 주간 알림 횟수 2회 미만
        val eligibleByNotificationCount =
            activeUsers.filter { user ->
                val count = notificationCountMap[user.userId] ?: 0
                count < MAX_NOTIFICATIONS_PER_WEEK
            }

        logger.info("${eligibleByNotificationCount.size} users have received less than $MAX_NOTIFICATIONS_PER_WEEK notifications this week")

        if (eligibleByNotificationCount.isEmpty()) {
            return emptyList()
        }

        // 4. 필터링: 위치 기반 (3km 반경 내 개화 상태)
        val eligibleByLocation =
            eligibleByNotificationCount.filter { user ->
                locationChecker.hasNearbyBloomingSpots(user.latitude, user.longitude)
            }

        logger.info("${eligibleByLocation.size} users have nearby blooming spots")

        if (eligibleByLocation.isEmpty()) {
            return emptyList()
        }

        // 5. 필터링: 대기질 (PM10 < 81µg/m³)
        val eligibleUsers =
            eligibleByLocation.filter { user ->
                airQualityChecker.hasGoodAirQuality(user.latitude, user.longitude)
            }

        logger.info("${eligibleUsers.size} users have good air quality")

        return eligibleUsers
    }

    /**
     * 이번 주 월요일 00:00:00 반환
     */
    private fun getWeekStartDateTime(): LocalDateTime =
        LocalDateTime
            .now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .toLocalDate()
            .atStartOfDay()
}
