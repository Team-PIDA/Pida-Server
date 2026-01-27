package com.pida.landmark

import com.pida.support.extension.logger
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class LandmarkService(
    private val landmarkFinder: LandmarkFinder,
    private val landmarkAppender: LandmarkAppender,
    private val landmarkSearchClient: LandmarkSearchClient,
) {
    private val logger by logger()

    suspend fun searchLandmarks(query: String): List<Landmark> = landmarkFinder.searchByName(query)

    @Async
    @EventListener
    fun handleFetchEvent(event: LandmarkFetchEvent) =
        when (event) {
            is LandmarkFetchEvent.Requested -> {
                val landmarks =
                    try {
                        landmarkSearchClient.searchByKeyword(event.query)
                    } catch (e: Exception) {
                        logger.error("Failed to fetch landmarks for query='${event.query}'", e)
                        emptyList<NewLandmark>()
                    }

                addLandmarks(landmarks)
            }
            is LandmarkFetchEvent.Fetched -> {
                addLandmarks(event.landmarks)
            }
        }

    fun addLandmarks(landmarks: List<NewLandmark>) {
        val newLandmarks =
            landmarks
                .filterNot { landmarkFinder.existsByName(it.name) }

        if (newLandmarks.isNotEmpty()) {
            landmarkAppender.addAll(newLandmarks)
        }
    }
}
