package com.pida.flowerspot

interface FlowerSpotRepository {
    suspend fun findBy(spotId: Long): FlowerSpot

    suspend fun findAll(): List<FlowerSpot>

    suspend fun findAllByRegion(region: Region): List<FlowerSpot>

    suspend fun findAllByLocation(location: FlowerSpotLocation): List<FlowerSpot>

    suspend fun findAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot>
}
