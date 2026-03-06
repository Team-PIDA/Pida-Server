package com.pida.flowerspot

import org.springframework.stereotype.Component

@Component
class FlowerSpotCafeFinder(
    private val flowerSpotCafeRepository: FlowerSpotCafeRepository,
) {
    suspend fun readBy(cafeId: Long): FlowerSpotCafe = flowerSpotCafeRepository.findBy(cafeId)

    suspend fun readAllByFlowerSpotId(flowerSpotId: Long): List<FlowerSpotCafe> =
        flowerSpotCafeRepository.findAllByFlowerSpotId(flowerSpotId)
}
