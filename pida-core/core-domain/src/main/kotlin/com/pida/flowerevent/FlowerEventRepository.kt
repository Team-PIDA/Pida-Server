package com.pida.flowerevent

import com.pida.flowerspot.FlowerSpotLocation

interface FlowerEventRepository {
    suspend fun findAllByCategoryId(categoryId: Long): List<FlowerEvent>

    suspend fun findAllByCategoryIdAndLocation(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<FlowerEvent>
}
