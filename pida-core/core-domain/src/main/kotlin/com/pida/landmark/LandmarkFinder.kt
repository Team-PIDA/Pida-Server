package com.pida.landmark

import com.pida.support.cache.CacheAdvice
import org.springframework.stereotype.Component

@Component
class LandmarkFinder(
    private val landmarkRepository: LandmarkRepository,
    private val cacheAdvice: CacheAdvice,
) {
    fun findAll(): List<Landmark> = landmarkRepository.findAll()

    fun searchByName(query: String): List<Landmark> = landmarkRepository.findByNameContaining(query)

    fun existsByName(name: String): Boolean = landmarkRepository.existsByName(name)
}
