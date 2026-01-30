package com.pida.place

import com.pida.flowerspot.FlowerSpotSearchEvent
import com.pida.flowerspot.FlowerSpotSearchResult
import com.pida.flowerspot.FlowerSpotService
import com.pida.user.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PlaceFacade(
    private val flowerSpotService: FlowerSpotService,
    private val landmarkService: LandmarkService,
    private val landmarkSearchClient: LandmarkSearchClient,
    private val eventPublisher: ApplicationEventPublisher,
) {
    companion object {
        private const val MIN_SEARCH_RESULT_COUNT = 2
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
