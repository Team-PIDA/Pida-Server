package com.pida.blooming

import org.springframework.stereotype.Service

@Service
class BloomingService(
    private val bloomingAppender: BloomingAppender,
    private val bloomingValidator: BloomingValidator,
    private val bloomingFinder: BloomingFinder,
) {
    suspend fun add(newBlooming: NewBlooming): Blooming {
        val blooming = bloomingFinder.findByUserIdAndFlowerSpotId(newBlooming.userId, newBlooming.flowerSpotId)
        bloomingValidator.addValidate(blooming)

        return bloomingAppender.add(newBlooming)
    }

    suspend fun recentlyVisitedCountBySpotId(spotId: Long): Long = bloomingFinder.recentlyVisitedCountBySpotId(spotId)
}
