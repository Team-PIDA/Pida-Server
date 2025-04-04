package com.pida.blooming

import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.roundToInt

@Service
class BloomingService(
    private val bloomingAppender: BloomingAppender,
    private val bloomingValidator: BloomingValidator,
    private val bloomingFinder: BloomingFinder,
) {
    suspend fun add(newBlooming: NewBlooming): Blooming {
        val blooming = bloomingFinder.readTopByUserIdAndFlowerSpotIdDesc(newBlooming.userId, newBlooming.flowerSpotId)
        bloomingValidator.addValidate(blooming)

        return bloomingAppender.add(newBlooming)
    }

    suspend fun recentlyBloomingBySpotId(spotId: Long): List<Blooming> = bloomingFinder.readRecentlyBloomingBySpotId(spotId)

    fun recentlyBloomingBySpotIds(spotIds: List<Long>): List<Blooming> = bloomingFinder.recentlyBloomingBySpotIds(spotIds)

    suspend fun readAllBloomingDetailsBySpotId(spotId: Long): BloomingDetails {
        val bloomings = bloomingFinder.readRecentlyBloomingBySpotId(spotId)

        val totalCount = bloomings.size.toLong()

        val details: Map<String, Map<String, BloomingStatusDetails>> =
            bloomings
                .groupBy { it.createdAt.toLocalDate().toString() }
                .entries
                .sortedByDescending { LocalDate.parse(it.key) }
                .associate { (date, bloomingsOnDate) ->
                    val dailyTotal = bloomingsOnDate.size.toDouble()

                    val statusCounts =
                        bloomingsOnDate
                            .groupingBy { it.status.name }
                            .eachCount()

                    val statusDetailsMap =
                        statusCounts.mapValues { (_, count) ->
                            val percentage = if (dailyTotal == 0.0) 0 else ((count / dailyTotal) * 100).roundToInt()
                            BloomingStatusDetails(
                                peopleCount = count,
                                percentage = percentage,
                            )
                        }

                    date to statusDetailsMap
                }

        return BloomingDetails(
            totalCount = totalCount,
            details = details,
        )
    }

    fun verifyTodayBlooming(
        userId: Long,
        spotId: Long,
    ): Boolean {
        val blooming = bloomingFinder.readTodayBloomingByUserId(userId, spotId)
        return bloomingValidator.todayBloomingValidate(blooming)
    }
}
