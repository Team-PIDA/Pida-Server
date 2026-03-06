package com.pida.place

interface DistrictRepository {
    fun saveAll(districts: List<District>)

    fun searchByKeyword(keyword: String): List<District>

    /**
     * 주어진 좌표에서 가장 가까운 District를 찾습니다.
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return 가장 가까운 District, 없으면 null
     */
    fun findNearestDistrict(
        latitude: Double,
        longitude: Double,
    ): District?
}
