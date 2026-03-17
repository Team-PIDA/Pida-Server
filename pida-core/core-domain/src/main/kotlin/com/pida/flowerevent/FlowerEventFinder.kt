package com.pida.flowerevent

import org.springframework.stereotype.Component

@Component
class FlowerEventFinder(
    private val flowerEventRepository: FlowerEventRepository,
) {
    suspend fun readAllByCategoryId(categoryId: Long): List<FlowerEvent> = flowerEventRepository.findAllByCategoryId(categoryId)
}
