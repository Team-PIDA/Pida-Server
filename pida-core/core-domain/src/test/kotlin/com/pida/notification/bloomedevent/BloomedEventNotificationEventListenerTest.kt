package com.pida.notification.bloomedevent

import com.pida.blooming.BloomingAddedEvent
import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BloomedEventNotificationEventListenerTest {
    @Test
    fun `꽃 이벤트 BLOOMED 투표면 즉시 알림을 발송한다`() {
        val bloomedEventNotificationService = mockk<BloomedEventNotificationService>()
        val listener = BloomedEventNotificationEventListener(bloomedEventNotificationService)

        every { bloomedEventNotificationService.sendBloomedEventNotification(21L) } returns Unit

        listener.handleBloomingAddedEvent(
            BloomingAddedEvent(
                NewBlooming.FlowerEvent(
                    userId = 1L,
                    flowerEventId = 21L,
                    status = BloomingStatus.BLOOMED,
                ),
            ),
        )

        verify { bloomedEventNotificationService.sendBloomedEventNotification(21L) }
    }
}
