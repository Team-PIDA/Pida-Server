package com.pida.flowerspot

interface FlowerSpotRepository {
    fun findBy(spotId: Long): FlowerSpot

    suspend fun findAll(): List<FlowerSpot>

    suspend fun findAllByRegion(region: Region): List<FlowerSpot>
}
