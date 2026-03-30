package com.pida.flowerspot

import org.springframework.stereotype.Component

@Component
class FlowerSpotCafeFinder(
    private val flowerSpotCafeRepository: FlowerSpotCafeRepository,
) {
    suspend fun readBy(cafeId: Long): FlowerSpotCafe = flowerSpotCafeRepository.findBy(cafeId)

    suspend fun readAll(): List<FlowerSpotCafe> = flowerSpotCafeRepository.findAll()

    suspend fun readAllByLocation(location: FlowerSpotLocation): List<FlowerSpotCafe> = flowerSpotCafeRepository.findAllByLocation(location)
}
