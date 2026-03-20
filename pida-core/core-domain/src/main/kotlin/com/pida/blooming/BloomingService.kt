package com.pida.blooming

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
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

    suspend fun recentlyBloomingByEventId(eventId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingByEventId(eventId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> = bloomingFinder.recentlyBloomingBySpotIds(spotIds)

    fun recentlyBloomingByEventIds(eventIds: List<Long>): List<Blooming> = bloomingFinder.recentlyBloomingByEventIds(eventIds)

    fun verifyTodayBlooming(
        userId: Long,
        flowerSpotId: Long? = null,
        flowerEventId: Long? = null,
    ): Boolean {
        val blooming =
            when {
                flowerSpotId != null && flowerEventId == null -> bloomingFinder.readTodayBloomingByUserId(userId, flowerSpotId)
                flowerSpotId == null && flowerEventId != null ->
                    bloomingFinder.readTodayBloomingByUserIdAndFlowerEventId(
                        userId,
                        flowerEventId,
                    )

                else -> throw ErrorException(ErrorType.INVALID_REQUEST)
            }

        return bloomingValidator.todayBloomingValidate(blooming)
    }
}
