package com.pida.notification.bloomedevent

import com.pida.blooming.BloomingAddedEvent
import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * BLOOMED 투표 이벤트를 수신하여 꽃 이벤트 반경 알림을 발송합니다.
 */
@Component
class BloomedEventNotificationEventListener(
    private val bloomedEventNotificationService: BloomedEventNotificationService,
) {
    @Async
    @EventListener
    fun handleBloomingAddedEvent(event: BloomingAddedEvent) {
        val newBlooming = event.newBlooming as? NewBlooming.FlowerEvent ?: return

        if (newBlooming.status != BloomingStatus.BLOOMED) {
            return
        }

        bloomedEventNotificationService.sendBloomedEventNotification(newBlooming.flowerEventId)
    }
}
