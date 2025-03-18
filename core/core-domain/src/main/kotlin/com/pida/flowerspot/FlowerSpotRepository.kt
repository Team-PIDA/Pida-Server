package com.pida.flowerspot

interface FlowerSpotRepository {
    fun findBy(spotId: Long): FlowerSpot
}
