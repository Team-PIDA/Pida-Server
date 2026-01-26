package com.pida.flowerspot

import com.pida.blooming.BloomingService
import com.pida.landmark.LandmarkSearchClient
import com.pida.landmark.LandmarkService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.geo.Region
import com.pida.user.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class FlowerSpotFacade(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
    private val landmarkService: LandmarkService,
    private val landmarkSearchClient: LandmarkSearchClient,
    private val imageS3Caller: ImageS3Caller,
) {
    companion object {
        private const val MIN_SEARCH_RESULT_COUNT = 2
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

    fun search(
        query: String,
        user: User,
    ): FlowerSpotSearchResult {
        val landmarks = landmarkService.searchLandmarks(query)
        val flowerSpots = flowerSpotService.searchFlowerSpots(query)

        // 랜드마크 데이터가 충분하면 바로 응답
        if (landmarks.size >= MIN_SEARCH_RESULT_COUNT) {
            return FlowerSpotSearchResult(landmarks, flowerSpots)
        }
        // 보정용 외부 API 이벤트 발행

        // 데이터가 부족하면 외부 API 응답을 대기
        val apiLandmarks = landmarkSearchClient.searchByKeyword(query)
        landmarkService.addLandmarks(apiLandmarks)

        return FlowerSpotSearchResult(
            landmarks = apiLandmarks.map { it.toLandmark() },
            flowerSpots = flowerSpots,
        )
    }
}
