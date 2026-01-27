package com.pida.notification.weekend

import com.pida.blooming.BloomingRepository
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.support.extension.logger
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

/**
 * 주말 알림 위치 기반 필터링 컴포넌트
 *
 * 사용자 위치 근처에 개화 상태인 FlowerSpot이 있는지 확인
 */
@Component
class WeekendNotificationLocationChecker(
    private val flowerSpotRepository: FlowerSpotRepository,
    private val bloomingRepository: BloomingRepository,
) {
    private val logger by logger()

    companion object {
        const val RADIUS_METERS = 3000.0 // 3km
    }

    /**
     * 사용자 위치 근처에 개화 상태인 FlowerSpot이 있는지 확인
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return 근처에 개화 상태인 FlowerSpot이 있으면 true
     */
    fun hasNearbyBloomingSpots(
        latitude: Double,
        longitude: Double,
    ): Boolean =
        runBlocking {
            // 1. 반경 3km 내 FlowerSpot 조회
            val nearbySpots =
                flowerSpotRepository.findWithinRadius(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = RADIUS_METERS,
                )

            if (nearbySpots.isEmpty()) {
                logger.debug("No flower spots found within ${RADIUS_METERS}m of ($latitude, $longitude)")
                return@runBlocking false
            }

            logger.debug("Found ${nearbySpots.size} flower spots within ${RADIUS_METERS}m")

            // 2. 해당 spot들 중 개화 상태인 것이 있는지 확인
            val spotIds = nearbySpots.map { it.id }
            val bloomedSpotIds = bloomingRepository.findBloomedSpotIdsByFlowerSpotIds(spotIds)

            val hasBloomingSpots = bloomedSpotIds.isNotEmpty()

            if (hasBloomingSpots) {
                logger.debug("Found ${bloomedSpotIds.size} blooming spots near ($latitude, $longitude)")
            } else {
                logger.debug("No blooming spots found near ($latitude, $longitude)")
            }

            hasBloomingSpots
        }
}
