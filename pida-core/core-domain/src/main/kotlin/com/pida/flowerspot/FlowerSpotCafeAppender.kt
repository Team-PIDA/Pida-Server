package com.pida.flowerspot

import org.springframework.stereotype.Component

@Component
class FlowerSpotCafeAppender(
    private val flowerSpotCafeRepository: FlowerSpotCafeRepository,
) {
    suspend fun add(cafe: FlowerSpotCafe): FlowerSpotCafe = flowerSpotCafeRepository.save(cafe)

    suspend fun updateThumbnailUrl(
        cafeId: Long,
        thumbnailUrl: String,
    ) {
        flowerSpotCafeRepository.updateThumbnailUrl(cafeId, thumbnailUrl)
    }
}
