package com.pida.notification.bloomed

import com.pida.blooming.BloomingAddedEvent
import com.pida.blooming.BloomingStatus
import com.pida.support.extension.logger
import com.pida.support.geo.GeoJson
import com.pida.user.location.UserLocationReader
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * BLOOMED 추가 이벤트를 수신하여 지역 첫 투표 조건에 맞으면 알림을 발송합니다.
 */
@Service
class BloomedFirstVoteNotificationEventListener(
    private val userLocationReader: UserLocationReader,
    private val regionResolver: BloomedRegionResolver,
    private val firstVoteChecker: BloomedFirstVoteChecker,
    private val bloomedNotificationService: BloomedNotificationService,
) {
    private val logger by logger()

    @Async
    @EventListener
    fun handleBloomingAddedEvent(event: BloomingAddedEvent) {

        if (event.newBlooming.status != BloomingStatus.BLOOMED) {
            return
        }

        val userLocation = userLocationReader.readUserLocationByUserId(event.newBlooming.userId) ?: return
        val point = userLocation.location as? GeoJson.Point ?: return

        val userRegion =
            regionResolver.resolveRegion(
                latitude = point.coordinates[1],
                longitude = point.coordinates[0],
            ) ?: return

        if (!firstVoteChecker.isFirstBloomedVoteOfYear(userRegion)) {
            return
        }

        bloomedNotificationService.sendBloomedNotificationForRegion(userRegion)
    }
}
