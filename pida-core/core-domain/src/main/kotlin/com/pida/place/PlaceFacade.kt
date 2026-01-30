package com.pida.place

import com.pida.flowerspot.FlowerSpotSearchEvent
import com.pida.flowerspot.FlowerSpotService
import com.pida.user.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PlaceFacade(
    private val flowerSpotService: FlowerSpotService,
    private val landmarkService: LandmarkService,
    private val districtService: DistrictService,
    private val landmarkSearchClient: LandmarkSearchClient,
    private val eventPublisher: ApplicationEventPublisher,
) {
    companion object {
        private const val MAX_DISTRICT_SEARCH_COUNT = 2
        private const val MIN_LANDMARK_SEARCH_COUNT = 2
        private const val MAX_LANDMARK_SEARCH_COUNT = 5
    }

    suspend fun search(
        query: String,
        user: User?,
    ): PlaceSearchResult =
        coroutineScope {
            publishSearchEvent(query, user)

            val districtsDeferred = async { districtService.searchDistricts(query) }
            val landmarksDeferred = async { landmarkService.searchLandmarks(query) }
            val flowerSpotsDeferred = async { flowerSpotService.searchFlowerSpots(query) }

            val storedLandmarks = landmarksDeferred.await()
            val landmarks =
                if (hasEnough(storedLandmarks)) {
                    // 저장된 랜드마크가 충분한 경우 즉시 응답 후에 비동기적으로 보정
                    publishLandmarkFetchEvent(query, null)
                    storedLandmarks
                } else {
                    // 저장된 랜드마크가 충분하지 않은 경우, 외부 API 호출 대기
                    val fetched = landmarkSearchClient.searchByKeyword(query)
                    publishLandmarkFetchEvent(query, fetched)
                    fetched.map { it.toLandmark() }
                }

            PlaceSearchResult(
                districts = districtsDeferred.await().take(MAX_DISTRICT_SEARCH_COUNT),
                landmarks = landmarks.take(MAX_LANDMARK_SEARCH_COUNT),
                flowerSpots = flowerSpotsDeferred.await(),
            )
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

    private fun hasEnough(landmarks: List<Landmark>) = landmarks.size >= MIN_LANDMARK_SEARCH_COUNT
}
