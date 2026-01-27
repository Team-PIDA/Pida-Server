package com.pida.notification.weekend

import com.pida.auth.AuthenticationHistoryRepository
import com.pida.support.extension.logger
import com.pida.support.geo.GeoJson
import com.pida.user.location.UserLocation
import com.pida.user.location.UserLocationReader
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 주말 알림 대상 사용자 조회 컴포넌트
 */
@Component
class WeekendNotificationUserReader(
    private val authenticationHistoryRepository: AuthenticationHistoryRepository,
    private val userLocationReader: UserLocationReader,
) {
    private val logger by logger()

    companion object {
        private const val ACTIVE_USER_DAYS = 30L
    }

    /**
     * 최근 30일 내 활성 사용자 중 위치 정보가 있는 사용자 목록 조회
     *
     * @return 대상 사용자 목록
     */
    fun findActiveUsersWithLocation(): List<EligibleUser> {
        val thirtyDaysAgo = LocalDateTime.now().minusDays(ACTIVE_USER_DAYS)

        // 1. 최근 30일 내 로그인한 활성 사용자 ID 조회
        val activeUserIds = authenticationHistoryRepository.findActiveUsersSince(thirtyDaysAgo)

        logger.info("Found ${activeUserIds.size} active users in last $ACTIVE_USER_DAYS days")

        if (activeUserIds.isEmpty()) {
            return emptyList()
        }

        // 2. 활성 사용자들의 위치 정보 일괄 조회 (N+1 문제 해결)
        val userLocations: List<UserLocation.Info> = userLocationReader.readUserLocationsByUserIds(activeUserIds).distinctBy { it.userId }

        logger.info("Found ${userLocations.size} user locations")

        // 3. UserLocation을 EligibleUser로 변환
        val eligibleUsers =
            userLocations.mapNotNull { userLocation: UserLocation.Info ->
                // GeoJson.Point로 캐스팅하여 coordinates 접근
                val point = userLocation.location as? GeoJson.Point
                point?.let {
                    EligibleUser(
                        userId = userLocation.userId,
                        latitude = it.coordinates[1], // GeoJson Point: [longitude, latitude]
                        longitude = it.coordinates[0],
                    )
                }
            }

        logger.info("${eligibleUsers.size} users have location information")

        return eligibleUsers
    }
}
