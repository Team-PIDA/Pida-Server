package com.pida.flowerspot

interface FlowerSpotCafeRepository {
    suspend fun findBy(cafeId: Long): FlowerSpotCafe

    suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<FlowerSpotCafe>
}
