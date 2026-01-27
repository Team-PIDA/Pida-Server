package com.pida.flowerspot

import com.pida.support.geo.Region

interface FlowerSpotRepository {
    suspend fun findBy(spotId: Long): FlowerSpot

    suspend fun findAll(): List<FlowerSpot>

    suspend fun findAllByRegion(region: Region): List<FlowerSpot>

    suspend fun findAllByLocation(location: FlowerSpotLocation): List<FlowerSpot>

    suspend fun findAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot>

    fun findByStreetNameContaining(streetName: String): List<FlowerSpot>
}
