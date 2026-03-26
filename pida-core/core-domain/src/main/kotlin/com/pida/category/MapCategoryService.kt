package com.pida.category

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.CacheAdvice
import org.springframework.stereotype.Service

@Service
class MapCategoryService(
    private val mapCategoryRepository: MapCategoryRepository,
    private val cacheAdvice: CacheAdvice,
) {
    companion object {
        private const val ALL_CATEGORY_KEY = "category:all"
        private const val CATEGORY_TTL = 600L
    }

    suspend fun readBy(categoryId: Long): MapCategory = mapCategoryRepository.findBy(categoryId)

    suspend fun findAll(): List<MapCategory> =
        cacheAdvice.invoke(
            ttl = CATEGORY_TTL,
            key = ALL_CATEGORY_KEY,
            typeReference = object : TypeReference<List<MapCategory>>() {},
        ) {
            mapCategoryRepository.findAll()
        }

    suspend fun findAllByCategoryLabel(categoryLabel: CategoryLabel): List<MapCategory> =
        findAll().filter { it.categoryLabel == categoryLabel }
}
