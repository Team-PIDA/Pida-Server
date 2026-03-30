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

    suspend fun readTopByUserIdAndFlowerSpotCafeIdDesc(
        userId: Long,
        flowerSpotCafeId: Long,
    ): Blooming? = bloomingRepository.findTopByUserIdAndCafeIdDesc(userId, flowerSpotCafeId)

    suspend fun readAllByUserId(userId: Long): List<Blooming> = bloomingRepository.findAllByUserId(userId)

    suspend fun readAllByFlowerSpotId(flowerSpotId: Long): List<Blooming> = bloomingRepository.findAllByFlowerSpotId(flowerSpotId)

    suspend fun readRecentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingRepository.findRecentlyBySpotId(spotId)

    suspend fun readRecentlyBloomingByEventId(eventId: Long): List<Blooming> = bloomingRepository.findRecentlyByEventId(eventId)

    suspend fun readRecentlyBloomingByCafeId(cafeId: Long): List<Blooming> = bloomingRepository.findRecentlyByCafeId(cafeId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> = bloomingRepository.findRecentBySpotIds(spotIds)

    fun recentlyBloomingByEventIds(eventIds: List<Long>): List<Blooming> = bloomingRepository.findRecentByEventIds(eventIds)

    fun recentlyBloomingByCafeIds(cafeIds: List<Long>): List<Blooming> = bloomingRepository.findRecentByCafeIds(cafeIds)

    fun readTodayBloomingByUserId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? = bloomingRepository.findTodayBloomingByUserId(userId, flowerSpotId)

    fun readTodayBloomingByUserIdAndFlowerEventId(
        userId: Long,
        flowerEventId: Long,
    ): Blooming? = bloomingRepository.findTodayEventBloomingByUserId(userId, flowerEventId)

    fun readTodayBloomingByUserIdAndFlowerSpotCafeId(
        userId: Long,
        flowerSpotCafeId: Long,
    ): Blooming? = bloomingRepository.findTodayCafeBloomingByUserId(userId, flowerSpotCafeId)
}
