package com.pida.flowerspot

import com.pida.blooming.BloomingService
import com.pida.landmark.*
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.geo.Region
import com.pida.user.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class FlowerSpotFacade(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
    private val landmarkService: LandmarkService,
    private val landmarkSearchClient: LandmarkSearchClient,
    private val imageS3Caller: ImageS3Caller,
    private val eventPublisher: ApplicationEventPublisher,
) {
    companion object {
        private const val MIN_SEARCH_RESULT_COUNT = 3
    }

    suspend fun readFlowerSpotDetails(spotId: Long): FlowerSpotDetails =
        coroutineScope {
            val flowerSpotDeferred = async { flowerSpotService.readOneFlowerSpot(spotId) }
            val bloomings = async { bloomingService.recentlyBloomingBySpotId(spotId) }
            val imageUrls =
                async {
                    imageS3Caller.getImageUrl(
                        prefix = ImagePrefix.FLOWERSPOT.value,
                        prefixId = spotId,
                        fileName = null,
                    )
                }

            return@coroutineScope FlowerSpotDetails.of(
                flowerSpot = flowerSpotDeferred.await(),
                bloomings = bloomings.await().groupBy { it.flowerSpotId }[spotId] ?: emptyList(),
                imageUrls = imageUrls.await(),
            )
        }

    suspend fun findAllFlowerSpot(
        region: Region?,
        location: FlowerSpotLocation,
    ): List<FlowerSpotDetails> {
        val flowerSpots = flowerSpotService.readAllFlowerSpot(region, location)
        val recentlyBlooming = bloomingService.recentlyBloomingBySpotIds(flowerSpots.map { it.id })

        return flowerSpots.map { flowerSpot ->
            FlowerSpotDetails.of(
                flowerSpot = flowerSpot,
                bloomings = recentlyBlooming.groupBy { it.flowerSpotId }[flowerSpot.id] ?: emptyList(),
                imageUrls =
                    imageS3Caller.getImageUrl(
                        prefix = ImagePrefix.FLOWERSPOT.value,
                        prefixId = flowerSpot.id,
                        fileName = null,
                    ),
            )
        }
    }

    suspend fun search(
        query: String,
        user: User?,
    ): FlowerSpotSearchResult {
        publishSearchEvent(query, user)

        val flowerSpots = flowerSpotService.searchFlowerSpots(query)
        val cachedLandmarks = landmarkService.searchLandmarks(query)

        val landmarks =
            if (hasEnough(cachedLandmarks)) {
                // 캐시된 랜드마크가 충분한 경우 즉시 응답 후에 비동기적으로 보정
                publishLandmarkFetchEvent(query, null)
                cachedLandmarks
            } else {
                // 캐시된 랜드마크가 충분하지 않은 경우, 외부 API 호출 대기
                val fetched = landmarkSearchClient.searchByKeyword(query)
                publishLandmarkFetchEvent(query, fetched)
                fetched.map { it.toLandmark() }
            }

        return FlowerSpotSearchResult(landmarks, flowerSpots)
    }

    private fun publishLandmarkFetchEvent(
        query: String,
        fetchedLandmark: List<NewLandmark>?,
    ) {
        eventPublisher.publishEvent(
            LandmarkFetchEvent.from(query, fetchedLandmark),
        )
    }

    private fun publishSearchEvent(
        query: String,
        user: User?,
    ) {
        eventPublisher.publishEvent(
            FlowerSpotSearchEvent(
                query = query,
                userId = user?.id,
            ),
        )
    }

    private fun hasEnough(landmarks: List<Landmark>) = landmarks.size >= MIN_SEARCH_RESULT_COUNT
}
