package com.pida.blooming

interface BloomingRepository {
    fun add(newBlooming: NewBlooming): Blooming

    suspend fun findTopByUserIdAndSpotIdDecs(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming?

    suspend fun findAllByUserId(userId: Long): List<Blooming>

    suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming>

    suspend fun findRecentlyBySpotId(spotId: Long): List<Blooming>

    fun findRecentBySpotIds(spotIds: List<Long>): List<Blooming>

    fun findTodayBloomingByUserId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming?

    /**
     * 주어진 FlowerSpot ID 목록 중 BLOOMED 상태인 spot ID 목록 조회
     *
     * @param spotIds FlowerSpot ID 목록
     * @return BLOOMED 상태인 spot ID 목록
     */
    fun findBloomedSpotIdsByFlowerSpotIds(spotIds: List<Long>): List<Long>
}
