package com.pida.place

import org.springframework.stereotype.Service

@Service
class DistrictService(
    private val districtAppender: DistrictAppender,
    private val districtFinder: DistrictFinder,
) {
    fun addAll(districts: List<District>) {
        districtAppender.addAll(districts)
    }

    suspend fun searchDistricts(query: String): List<District> {
        if (query.isBlank()) return emptyList() // 빈 쿼리인 경우 빈 리스트 반환
        val keywords = query.trim().split("\\s+".toRegex()) // 공백 제거 후 단어별로 분리
        val results = districtFinder.searchByKeyword(keywords.first()) // 첫 번째 키워드로 검색

        if (keywords.size == 1) return results

        return results.filter { district ->
            // 나머지 키워드들로 필터링
            val fullText =
                listOfNotNull(district.sido, district.sigungu, district.eupmyeondonggu, district.eupmyeonridong, district.ri)
                    .joinToString(" ")
            keywords.drop(1).all { keyword -> fullText.contains(keyword) }
        }
    }
}
