package com.pida.flowerevent

import org.springframework.stereotype.Component

@Component
class FlowerEventAppender(
    private val flowerEventRepository: FlowerEventRepository,
) {
    suspend fun add(event: FlowerEvent): FlowerEvent = flowerEventRepository.save(event)

    suspend fun updateThumbnailUrl(
        eventId: Long,
        thumbnailUrl: String,
    ) {
        flowerEventRepository.updateThumbnailUrl(eventId, thumbnailUrl)
    }
}
