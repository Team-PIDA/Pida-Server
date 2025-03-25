package com.pida.blooming

interface BloomingRepository {
    fun add(newBlooming: NewBlooming): Blooming

    suspend fun findByUserIdAndSpotId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming?

    suspend fun findAllByUserId(userId: Long): List<Blooming>

    suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming>

    suspend fun countRecentBySpotId(spotId: Long): Long
}
