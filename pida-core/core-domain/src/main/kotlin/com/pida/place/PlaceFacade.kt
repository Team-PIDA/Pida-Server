package com.pida.place

import com.pida.flowerspot.FlowerSpotSearchEvent
import com.pida.flowerspot.FlowerSpotService
import com.pida.support.geo.Region
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
        private val SEARCH_REGIONS = setOf(Region.SEOUL, Region.GYEONGGI)
        private val LANDMARK_CATEGORY_PRIORITY =
            listOf(
                LandmarkCategory.SUBWAY,
                // 정렬 우선순위 여기에 추가
            )
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
                    fetched.map { it.toLandmark() }.filter { it.region in SEARCH_REGIONS }
                }

            PlaceSearchResult(
                districts = districtsDeferred.await().take(MAX_DISTRICT_SEARCH_COUNT),
                landmarks =
                    landmarks
                        .filter { it.region in SEARCH_REGIONS }
                        .sortedBy { LANDMARK_CATEGORY_PRIORITY.indexOf(it.category).takeIf { i -> i >= 0 } ?: Int.MAX_VALUE }
                        .take(MAX_LANDMARK_SEARCH_COUNT),
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
