package com.pida.category

import org.springframework.stereotype.Service

@Service
class MapCategoryService(
    private val mapCategoryRepository: MapCategoryRepository,
) {
    suspend fun findAll(): List<MapCategory> = mapCategoryRepository.findAll()
}
