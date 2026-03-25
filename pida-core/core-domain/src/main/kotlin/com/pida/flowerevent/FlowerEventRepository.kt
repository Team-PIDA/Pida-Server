package com.pida.flowerevent

import com.pida.flowerspot.FlowerSpotLocation

interface FlowerEventRepository {
    suspend fun findBy(eventId: Long): FlowerEvent

    suspend fun findAllByCategoryId(categoryId: Long): List<FlowerEvent>

    suspend fun findAllByCategoryIdAndLocation(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<FlowerEvent>

    suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ): List<FlowerEvent>

    suspend fun save(event: FlowerEvent): FlowerEvent

    suspend fun updateThumbnailUrl(
        eventId: Long,
        thumbnailUrl: String,
    )
}
