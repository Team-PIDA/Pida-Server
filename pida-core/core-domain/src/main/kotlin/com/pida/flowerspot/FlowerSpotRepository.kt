package com.pida.flowerspot

interface FlowerSpotRepository {
    suspend fun findBy(spotId: Long): FlowerSpot

    suspend fun findAll(): List<FlowerSpot>

    suspend fun findAllByRegion(region: Region): List<FlowerSpot>

    suspend fun findAllByLocation(location: FlowerSpotLocation): List<FlowerSpot>

    suspend fun findAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot>

    /**
     * 주어진 좌표 기준 반경 내의 FlowerSpot 목록 조회
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusMeters 반경 (미터 단위)
     * @return FlowerSpot 목록 (거리 순으로 정렬)
     */
    suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ): List<FlowerSpot>
}
