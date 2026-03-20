package com.pida.notification.bloomed

import com.pida.blooming.BloomingAddedEvent
import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import com.pida.flowerevent.FlowerEventRepository
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.support.extension.logger
import com.pida.support.geo.Region
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * BLOOMED 추가 이벤트를 수신하여 지역 첫 투표 조건에 맞으면 알림을 발송합니다.
 */
@Service
class BloomedFirstVoteNotificationEventListener(
    private val flowerSpotRepository: FlowerSpotRepository,
    private val flowerEventRepository: FlowerEventRepository,
    private val firstVoteChecker: BloomedFirstVoteChecker,
    private val bloomedNotificationService: BloomedNotificationService,
) {
    private val logger by logger()

    @Async
    @EventListener
    fun handleBloomingAddedEvent(event: BloomingAddedEvent) {
        val newBlooming = event.newBlooming

        if (newBlooming.status != BloomingStatus.BLOOMED) {
            return
        }

        val targetRegion = readTargetRegion(newBlooming) ?: return

        if (!firstVoteChecker.isFirstBloomedVoteOfYear(targetRegion)) {
            return
        }

        bloomedNotificationService.sendBloomedNotificationForRegion(targetRegion)
    }

    private fun readTargetRegion(newBlooming: NewBlooming): Region? =
        runCatching {
            runBlocking {
                when (newBlooming) {
                    is NewBlooming.FlowerSpot -> flowerSpotRepository.findBy(newBlooming.flowerSpotId).region
                    is NewBlooming.FlowerEvent -> flowerEventRepository.findBy(newBlooming.flowerEventId).region
                }
            }
        }.onFailure { error ->
            logger.error("Failed to resolve blooming target region for notification", error)
        }.getOrNull()
}
