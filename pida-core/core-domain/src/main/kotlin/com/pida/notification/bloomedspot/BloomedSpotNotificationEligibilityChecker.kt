package com.pida.notification.bloomedspot

import com.pida.flowerspot.FlowerSpot
import com.pida.notification.NotificationStoredRepository
import com.pida.notification.NotificationType
import com.pida.support.extension.logger
import com.pida.support.geo.GeoJson
import com.pida.user.location.UserLocationReader
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 벚꽃길 만개 이벤트 알림 적격성 검증 컴포넌트
 */
@Component
class BloomedSpotNotificationEligibilityChecker(
    private val userLocationReader: UserLocationReader,
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    private val logger by logger()

    companion object {
        private const val RADIUS_METERS = 3000.0
    }

    /**
     * 특정 벚꽃길 만개 이벤트에 대해 푸시 발송 가능한 사용자 ID 목록 조회
     *
     * 조건:
     * - 위치 권한 허용 사용자(위치 저장 이력 존재)
     * - 벚꽃길 반경 3km 이내
     * - 당일 이미 푸시를 받은 사용자는 제외
     * - 해당 벚꽃길 만개 알림을 같은 시즌(연도)에 받은 사용자는 제외
     */
    fun findEligibleUserIds(flowerSpot: FlowerSpot): List<Long> {
        val pinPoint = flowerSpot.pinPoint as? GeoJson.Point

        if (pinPoint == null || pinPoint.coordinates.size < 2) {
            logger.warn("Flower spot ${flowerSpot.id} has invalid pinPoint. Skip bloomed spot notification")
            return emptyList()
        }

        val longitude = pinPoint.coordinates[0]
        val latitude = pinPoint.coordinates[1]

        val nearbyUserIds =
            userLocationReader
                .readUserLocationsWithinRadius(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = RADIUS_METERS,
                ).map { it.userId }
                .distinct()

        if (nearbyUserIds.isEmpty()) {
            logger.info("No users found within ${RADIUS_METERS}m for flowerSpotId=${flowerSpot.id}")
            return emptyList()
        }

        val todayStart = LocalDate.now().atStartOfDay()
        val todayNotificationCountMap =
            notificationStoredRepository.countByUserIdsAndCreatedAtAfter(
                userIds = nearbyUserIds,
                createdAtAfter = todayStart,
            )

        val eligibleByToday =
            nearbyUserIds.filter { userId ->
                (todayNotificationCountMap[userId] ?: 0L) == 0L
            }

        if (eligibleByToday.isEmpty()) {
            logger.info("All nearby users were excluded by same-day push condition for flowerSpotId=${flowerSpot.id}")
            return emptyList()
        }

        val seasonStart =
            LocalDate
                .now()
                .withDayOfYear(1)
                .atStartOfDay()

        val spotNotificationCountMap =
            notificationStoredRepository.countByUserIdsAndTypeAndParameterValueAndCreatedAtAfter(
                userIds = eligibleByToday,
                type = NotificationType.BLOOMED_SPOT_ALERT,
                parameterValue = flowerSpot.id.toString(),
                createdAtAfter = seasonStart,
            )

        val eligibleUsers =
            eligibleByToday.filter { userId ->
                (spotNotificationCountMap[userId] ?: 0L) == 0L
            }

        logger.info(
            "Bloomed spot notification eligible users=${eligibleUsers.size}, flowerSpotId=${flowerSpot.id}, streetName=${flowerSpot.streetName}",
        )

        return eligibleUsers
    }
}
