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
                is NewBlooming.FlowerSpotCafe ->
                    bloomingFinder.readTopByUserIdAndFlowerSpotCafeIdDesc(
                        newBlooming.userId,
                        newBlooming.flowerSpotCafeId,
                    )
            }
        bloomingValidator.addValidate(blooming)

        return bloomingAppender.add(newBlooming)
    }

    suspend fun recentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingBySpotId(spotId)

    suspend fun recentlyBloomingByEventId(eventId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingByEventId(eventId)

    suspend fun recentlyBloomingByCafeId(cafeId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingByCafeId(cafeId)

    suspend fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> =
        bloomingFinder.recentlyBloomingBySpotIds(spotIds.distinct())

    suspend fun recentlyBloomingByEventIds(eventIds: List<Long>): List<Blooming> =
        bloomingFinder.recentlyBloomingByEventIds(eventIds.distinct())

    suspend fun recentlyBloomingByCafeIds(cafeIds: List<Long>): List<Blooming> =
        bloomingFinder.recentlyBloomingByCafeIds(cafeIds.distinct())

    fun verifyTodayBlooming(
        userId: Long,
        flowerSpotId: Long? = null,
        flowerEventId: Long? = null,
        flowerSpotCafeId: Long? = null,
    ): Boolean {
        val blooming =
            when {
                flowerSpotId != null && flowerEventId == null && flowerSpotCafeId == null ->
                    bloomingFinder.readTodayBloomingByUserId(userId, flowerSpotId)
                flowerSpotId == null && flowerEventId != null && flowerSpotCafeId == null ->
                    bloomingFinder.readTodayBloomingByUserIdAndFlowerEventId(userId, flowerEventId)
                flowerSpotId == null && flowerEventId == null && flowerSpotCafeId != null ->
                    bloomingFinder.readTodayBloomingByUserIdAndFlowerSpotCafeId(userId, flowerSpotCafeId)
                else -> throw ErrorException(ErrorType.INVALID_REQUEST)
            }

        return bloomingValidator.todayBloomingValidate(blooming)
    }
}
