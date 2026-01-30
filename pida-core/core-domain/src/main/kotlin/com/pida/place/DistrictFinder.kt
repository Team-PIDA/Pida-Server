package com.pida.place

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.support.cache.CacheAdvice
import org.springframework.stereotype.Component

@Component
class DistrictFinder(
    private val districtRepository: DistrictRepository,
    private val cacheAdvice: CacheAdvice,
) {
    companion object {
        private const val DISTRICT_PREFIX = "district"
        const val SEARCH_KEY = "$DISTRICT_PREFIX:search"
    }

    suspend fun searchByKeyword(keyword: String): List<District> =
        cacheAdvice.invoke(
            ttl = 180L,
            key = "$SEARCH_KEY:$keyword",
            typeReference = object : TypeReference<List<District>>() {},
        ) {
            districtRepository.searchByKeyword(keyword)
        }
}
