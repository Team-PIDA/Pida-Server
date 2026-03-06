package com.pida.blooming

import org.springframework.stereotype.Service

@Service
class BloomingService(
    private val bloomingAppender: BloomingAppender,
    private val bloomingValidator: BloomingValidator,
    private val bloomingFinder: BloomingFinder,
) {
    suspend fun add(newBlooming: NewBlooming): Blooming {
        val blooming =
            when (newBlooming) {
                is NewBlooming.FlowerSpot -> bloomingFinder.readTopByUserIdAndFlowerSpotIdDesc(newBlooming.userId, newBlooming.flowerSpotId)
                is NewBlooming.FlowerEvent ->
                    bloomingFinder.readTopByUserIdAndFlowerEventIdDesc(
                        newBlooming.userId,
                        newBlooming.flowerEventId,
                    )
            }
        bloomingValidator.addValidate(blooming)

        return bloomingAppender.add(newBlooming)
    }

    suspend fun recentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingBySpotId(spotId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> = bloomingFinder.recentlyBloomingBySpotIds(spotIds)

    fun verifyTodayBlooming(
        userId: Long,
        spotId: Long,
    ): Boolean {
        val blooming = bloomingFinder.readTodayBloomingByUserId(userId, spotId)
        return bloomingValidator.todayBloomingValidate(blooming)
    }
}
