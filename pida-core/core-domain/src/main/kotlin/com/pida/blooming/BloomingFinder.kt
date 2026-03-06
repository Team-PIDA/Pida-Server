package com.pida.blooming

import org.springframework.stereotype.Component

@Component
class BloomingFinder(
    private val bloomingRepository: BloomingRepository,
) {
    suspend fun readTopByUserIdAndFlowerSpotIdDesc(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? = bloomingRepository.findTopByUserIdAndSpotIdDesc(userId, flowerSpotId)

    suspend fun readTopByUserIdAndFlowerEventIdDesc(
        userId: Long,
        flowerEventId: Long,
    ): Blooming? = bloomingRepository.findTopByUserIdAndEventIdDesc(userId, flowerEventId)

    suspend fun readAllByUserId(userId: Long): List<Blooming> = bloomingRepository.findAllByUserId(userId)

    suspend fun readAllByFlowerSpotId(flowerSpotId: Long): List<Blooming> = bloomingRepository.findAllByFlowerSpotId(flowerSpotId)

    suspend fun readRecentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingRepository.findRecentlyBySpotId(spotId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> = bloomingRepository.findRecentBySpotIds(spotIds)

    fun readTodayBloomingByUserId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? = bloomingRepository.findTodayBloomingByUserId(userId, flowerSpotId)
}
