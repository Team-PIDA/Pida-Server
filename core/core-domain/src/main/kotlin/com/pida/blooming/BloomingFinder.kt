package com.pida.blooming

import org.springframework.stereotype.Component

@Component
class BloomingFinder(
    private val bloomingRepository: BloomingRepository,
) {
    suspend fun findByUserIdAndFlowerSpotId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? = bloomingRepository.findByUserIdAndSpotId(userId, flowerSpotId)

    suspend fun findAllByUserId(userId: Long): List<Blooming> = bloomingRepository.findAllByUserId(userId)

    suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming> = bloomingRepository.findAllByFlowerSpotId(flowerSpotId)

    suspend fun findRecentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingRepository.findRecentlyBySpotId(spotId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> = bloomingRepository.findRecentBySpotIds(spotIds)
}
