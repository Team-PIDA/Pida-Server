package com.pida.landmark

import org.springframework.stereotype.Component

@Component
class LandmarkAppender(
    private val landmarkRepository: LandmarkRepository,
) {
    fun addAll(newLandmarks: List<NewLandmark>) = landmarkRepository.saveAll(newLandmarks)
}
