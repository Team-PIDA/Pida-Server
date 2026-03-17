package com.pida.category

import org.springframework.stereotype.Service

@Service
class MapCategoryService(
    private val mapCategoryRepository: MapCategoryRepository,
) {
    suspend fun readBy(categoryId: Long): MapCategory = mapCategoryRepository.findBy(categoryId)

    suspend fun findAll(): List<MapCategory> = mapCategoryRepository.findAll()
}
