package com.pida.place

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.CacheAdvice
import org.springframework.stereotype.Component

@Component
class LandmarkFinder(
    private val landmarkRepository: LandmarkRepository,
    private val cacheAdvice: CacheAdvice,
) {
    companion object {
        private const val LANDMARK_PREFIX = "landmark"
        const val ALL_LANDMARKS = "$LANDMARK_PREFIX:all"
        const val SEARCH_KEY = "$LANDMARK_PREFIX:search"
    }

    suspend fun findAll(): List<Landmark> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = ALL_LANDMARKS,
            typeReference = object : TypeReference<List<Landmark>>() {},
        ) {
            landmarkRepository.findAll()
        }

    suspend fun searchByName(query: String): List<Landmark> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = "$SEARCH_KEY:$query",
            typeReference = object : TypeReference<List<Landmark>>() {},
        ) {
            landmarkRepository.findByNameContaining(query)
        }

    fun existsByName(name: String): Boolean = landmarkRepository.existsByName(name)
}
