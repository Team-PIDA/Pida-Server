package com.pida.flowerspot

interface FlowerSpotCafeRepository {
    suspend fun findBy(cafeId: Long): FlowerSpotCafe

    suspend fun findAll(): List<FlowerSpotCafe>

    suspend fun findAllByLocation(location: FlowerSpotLocation): List<FlowerSpotCafe>

    suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<FlowerSpotCafe>

    suspend fun save(cafe: FlowerSpotCafe): FlowerSpotCafe

    suspend fun updateThumbnailUrl(
        cafeId: Long,
        thumbnailUrl: String,
    )
}
