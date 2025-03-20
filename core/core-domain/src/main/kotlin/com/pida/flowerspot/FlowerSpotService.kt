package com.pida.flowerspot

import org.springframework.stereotype.Service

@Service
class FlowerSpotService(
    private val flowerSpotFinder: FlowerSpotFinder,
) {
    suspend fun findAllFlowerSpot(): List<FlowerSpot> = flowerSpotFinder.readAll()
}
