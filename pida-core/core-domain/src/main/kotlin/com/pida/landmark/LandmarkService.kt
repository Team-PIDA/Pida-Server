package com.pida.landmark

import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class LandmarkService(
    private val landmarkFinder: LandmarkFinder,
    private val landmarkAppender: LandmarkAppender,
    private val landmarkSearchClient: LandmarkSearchClient,
) {
    private val logger by logger()

    fun searchLandmarks(query: String): List<Landmark> = landmarkFinder.searchByName(query)

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

                logger.info("Fetched landmarks for query='${event.query}': ${landmarks.size} found")

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
