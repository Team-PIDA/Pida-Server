package com.pida.notification.weekend

import com.pida.notification.EligibleUser
import com.pida.support.extension.logger
import org.springframework.stereotype.Component

/**
 * 주말 알림 대상자 필터링 컴포넌트
 *
 * 여러 체커를 조합하여 최종 알림 발송 대상자를 선정
 */
@Component
class WeekendNotificationEligibilityChecker(
    private val weekendNotificationUserReader: WeekendNotificationUserReader,
    private val weekendNotificationLocationChecker: WeekendNotificationLocationChecker,
    private val weekendNotificationAirQualityChecker: WeekendNotificationAirQualityChecker,
) {
    private val logger by logger()

    /**
     * 주말 알림 발송 대상 사용자 조회
     *
     * 필터링 조건:
     * 1. 최근 30일 내 활성 사용자
     * 2. 사용자 위치 정보가 있는 사용자
     * 3. 사용자 위치 3km 반경 내 개화 상태인 FlowerSpot이 있는 사용자
     * 4. 사용자 위치의 미세먼지(PM10)가 나쁨(81µg/m³) 미만인 사용자
     *
     * @return 대상 사용자 목록
     */
    fun findEligibleUsers(): List<EligibleUser> {
        // Step 1: 최근 30일 내 활성 사용자 조회 (위치 정보 포함)
        val activeUsers = weekendNotificationUserReader.findActiveUsersWithLocation()

        if (activeUsers.isEmpty()) {
            return emptyList()
        }

        // Step 2: 근처에 개화 상태인 FlowerSpot이 있는 사용자 필터링
        val usersWithBloomingSpots =
            activeUsers.filter { user ->
                weekendNotificationLocationChecker.hasNearbyBloomingSpots(
                    user.latitude,
                    user.longitude,
                )
            }

        if (usersWithBloomingSpots.isEmpty()) {
            return emptyList()
        }

        // Step 3: 대기질이 좋은 사용자 필터링
        val eligibleUsers =
            usersWithBloomingSpots.filter { user ->
                weekendNotificationAirQualityChecker.hasGoodAirQuality(
                    user.latitude,
                    user.longitude,
                )
            }

        logger.info("${eligibleUsers.size} users passed air quality check")
        logger.info("Final eligible users: ${eligibleUsers.size}")

        return eligibleUsers
    }
}
