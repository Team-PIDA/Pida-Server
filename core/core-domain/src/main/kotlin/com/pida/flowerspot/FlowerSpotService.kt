package com.pida.flowerspot

import org.springframework.stereotype.Service

@Service
class FlowerSpotService(
    private val flowerSpotFinder: FlowerSpotFinder,
) {
    suspend fun findAllFlowerSpot(region: Region?): List<FlowerSpot> =
        if (region == null) {
            flowerSpotFinder.readAll()
        } else {
            flowerSpotFinder.readAllByRegion(region)
        }
}
