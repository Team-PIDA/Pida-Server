package com.pida.notification.bloomedspot

import com.pida.blooming.BloomingAddedEvent
import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * BLOOMED 투표 이벤트를 수신하여 벚꽃길 반경 알림을 발송합니다.
 */
@Component
class BloomedSpotNotificationEventListener(
    private val bloomedSpotNotificationService: BloomedSpotNotificationService,
) {
    @Async
    @EventListener
    fun handleBloomingAddedEvent(event: BloomingAddedEvent) {
        val newBlooming = event.newBlooming as? NewBlooming.FlowerSpot ?: return

        if (newBlooming.status != BloomingStatus.BLOOMED) {
            return
        }

        bloomedSpotNotificationService.sendBloomedSpotNotification(newBlooming.flowerSpotId)
    }
}
