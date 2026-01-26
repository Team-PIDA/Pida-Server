package com.pida.landmark

import org.springframework.stereotype.Service

@Service
class LandmarkService(
    private val landmarkFinder: LandmarkFinder,
    private val landmarkAppender: LandmarkAppender,
) {
    fun searchLandmarks(query: String): List<Landmark> = landmarkFinder.searchByName(query)

    fun addLandmarks(landmarks: List<NewLandmark>) {
        val newLandmarks =
            landmarks
                .filterNot { landmarkFinder.existsByName(it.name) }

        if (newLandmarks.isNotEmpty()) {
            landmarkAppender.addAll(newLandmarks)
        }
    }
}
