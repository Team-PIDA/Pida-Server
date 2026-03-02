package com.pida.blooming

import com.pida.reporter.RecentReporterService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.user.UserService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.roundToInt

@Service
class BloomingFacade(
    private val bloomingService: BloomingService,
    private val recentReporterService: RecentReporterService,
    private val userService: UserService,
    private val imageS3Caller: ImageS3Caller,
    private val eventPublisher: ApplicationEventPublisher,
) {
    suspend fun readBloomingDetailsBySpotId(flowerSpotId: Long): BloomingDetails =
        coroutineScope {
            val bloomingsDeferred = async { bloomingService.recentlyBloomingBySpotId(flowerSpotId) }
            val recentReporterDeferred = async { recentReporterService.findRecentlyByFlowerSpotId(flowerSpotId) }

            val userProfileDeferred =
                async {
                    val reporter = recentReporterDeferred.await()
                    reporter?.userId?.let { userService.getProfile(it) }
                }

            val bloomings = bloomingsDeferred.await()
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

            val recentReporter = recentReporterDeferred.await()
            val userProfile = userProfileDeferred.await()

            return@coroutineScope BloomingDetails(
                totalCount = totalCount,
                nickname = userProfile?.nickname,
                updatedAt = recentReporter?.updatedAt,
                details = details,
            )
        }

    suspend fun uploadBloomingStatus(newBlooming: NewBlooming): BloomingImageUploadUrl {
        bloomingService.add(newBlooming)
        eventPublisher.publishEvent(BloomingAddedEvent(newBlooming))

        return BloomingImageUploadUrl.from(
            imageS3Caller.createUploadUrl(newBlooming.userId, ImagePrefix.FLOWERSPOT.value, newBlooming.flowerSpotId),
        )
    }
}
