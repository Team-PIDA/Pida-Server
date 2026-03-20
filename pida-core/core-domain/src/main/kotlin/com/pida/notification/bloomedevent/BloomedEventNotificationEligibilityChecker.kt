package com.pida.notification.bloomedevent

import com.pida.flowerevent.FlowerEvent
import com.pida.notification.NotificationStoredRepository
import com.pida.notification.NotificationType
import com.pida.support.extension.logger
import com.pida.support.geo.GeoJson
import com.pida.user.location.UserLocationReader
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 꽃 이벤트 만개 알림 적격성 검증 컴포넌트
 */
@Component
class BloomedEventNotificationEligibilityChecker(
    private val userLocationReader: UserLocationReader,
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    private val logger by logger()

    companion object {
        private const val RADIUS_METERS = 3000.0
    }

    /**
     * 특정 꽃 이벤트 만개 이벤트에 대해 푸시 발송 가능한 사용자 ID 목록 조회
     */
    fun findEligibleUserIds(flowerEvent: FlowerEvent): List<Long> {
        val pinPoint = flowerEvent.pinPoint as? GeoJson.Point

        if (pinPoint == null || pinPoint.coordinates.size < 2) {
            logger.warn("Flower event ${flowerEvent.id} has invalid pinPoint. Skip bloomed event notification")
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
            logger.info("No users found within ${RADIUS_METERS}m for flowerEventId=${flowerEvent.id}")
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
            logger.info("All nearby users were excluded by same-day push condition for flowerEventId=${flowerEvent.id}")
            return emptyList()
        }

        val seasonStart =
            LocalDate
                .now()
                .withDayOfYear(1)
                .atStartOfDay()

        val eventNotificationCountMap =
            notificationStoredRepository.countByUserIdsAndTypeAndParameterValueAndCreatedAtAfter(
                userIds = eligibleByToday,
                type = NotificationType.BLOOMED_EVENT_ALERT,
                parameterValue = flowerEvent.id.toString(),
                createdAtAfter = seasonStart,
            )

        val eligibleUsers =
            eligibleByToday.filter { userId ->
                (eventNotificationCountMap[userId] ?: 0L) == 0L
            }

        logger.info(
            "Bloomed event notification eligible users=${eligibleUsers.size}, flowerEventId=${flowerEvent.id}, eventName=${flowerEvent.name}",
        )

        return eligibleUsers
    }
}
