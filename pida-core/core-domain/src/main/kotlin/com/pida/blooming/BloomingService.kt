package com.pida.blooming

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.Cache
import com.pida.support.cache.CacheRepository
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.stereotype.Service

@Service
class BloomingService(
    private val bloomingAppender: BloomingAppender,
    private val bloomingValidator: BloomingValidator,
    private val bloomingFinder: BloomingFinder,
    private val cacheRepository: CacheRepository,
) {
    companion object {
        const val BLOOMING_SPOT_KEY = "blooming:spot"
        const val BLOOMING_EVENT_KEY = "blooming:event"
        const val BLOOMING_TTL = 30L
    }

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

        val result = bloomingAppender.add(newBlooming)
        evictBloomingCache(newBlooming)
        return result
    }

    suspend fun recentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingBySpotId(spotId)

    suspend fun recentlyBloomingByEventId(eventId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingByEventId(eventId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> =
        spotIds.flatMap { spotId -> cachedRecentlyBloomingBySpotId(spotId) }

    fun recentlyBloomingByEventIds(eventIds: List<Long>): List<Blooming> =
        eventIds.flatMap { eventId -> cachedRecentlyBloomingByEventId(eventId) }

    private fun cachedRecentlyBloomingBySpotId(spotId: Long): List<Blooming> =
        Cache.cacheBlocking(
            ttl = BLOOMING_TTL,
            key = "$BLOOMING_SPOT_KEY:$spotId",
            typeReference = object : TypeReference<List<Blooming>>() {},
        ) {
            bloomingFinder.recentlyBloomingBySpotIds(listOf(spotId))
        }

    private fun cachedRecentlyBloomingByEventId(eventId: Long): List<Blooming> =
        Cache.cacheBlocking(
            ttl = BLOOMING_TTL,
            key = "$BLOOMING_EVENT_KEY:$eventId",
            typeReference = object : TypeReference<List<Blooming>>() {},
        ) {
            bloomingFinder.recentlyBloomingByEventIds(listOf(eventId))
        }

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

    private fun evictBloomingCache(newBlooming: NewBlooming) {
        when (newBlooming) {
            is NewBlooming.FlowerSpot -> cacheRepository.delete("$BLOOMING_SPOT_KEY:${newBlooming.flowerSpotId}")
            is NewBlooming.FlowerEvent -> cacheRepository.delete("$BLOOMING_EVENT_KEY:${newBlooming.flowerEventId}")
        }
    }
}
