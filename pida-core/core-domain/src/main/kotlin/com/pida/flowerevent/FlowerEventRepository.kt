package com.pida.flowerevent

interface FlowerEventRepository {
    suspend fun findAllByCategoryId(categoryId: Long): List<FlowerEvent>
}
