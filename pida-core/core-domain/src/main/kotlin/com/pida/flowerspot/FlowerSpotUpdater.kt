package com.pida.flowerspot

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FlowerSpotUpdater(
    private val flowerSpotRepository: FlowerSpotRepository,
) {
    suspend fun updatePreviewImageKey(
        spotId: Long,
        key: String,
    ) {
        flowerSpotRepository.updatePreviewImageKey(spotId, key, LocalDateTime.now())
    }
}
