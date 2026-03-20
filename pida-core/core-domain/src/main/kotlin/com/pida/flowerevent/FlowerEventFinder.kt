package com.pida.flowerevent

import com.pida.flowerspot.FlowerSpotLocation
import org.springframework.stereotype.Component

@Component
class FlowerEventFinder(
    private val flowerEventRepository: FlowerEventRepository,
) {
    suspend fun readBy(eventId: Long): FlowerEvent = flowerEventRepository.findBy(eventId)

    suspend fun readAllByCategoryId(categoryId: Long): List<FlowerEvent> = flowerEventRepository.findAllByCategoryId(categoryId)

    suspend fun readAllByCategoryIdAndLocation(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<FlowerEvent> = flowerEventRepository.findAllByCategoryIdAndLocation(categoryId, location)

    suspend fun readWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ): List<FlowerEvent> = flowerEventRepository.findWithinRadius(latitude, longitude, radiusMeters)
}
