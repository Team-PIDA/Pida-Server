package com.pida.blooming

import com.pida.support.geo.Region
import java.time.LocalDateTime

interface BloomingRepository {
    fun add(newBlooming: NewBlooming): Blooming

    suspend fun findTopByUserIdAndSpotIdDesc(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming?

    suspend fun findTopByUserIdAndEventIdDesc(
        userId: Long,
        flowerEventId: Long,
    ): Blooming?

    suspend fun findTopByUserIdAndCafeIdDesc(
        userId: Long,
        flowerSpotCafeId: Long,
    ): Blooming?

    suspend fun findAllByUserId(userId: Long): List<Blooming>

    suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming>

    suspend fun findRecentlyBySpotId(spotId: Long): List<Blooming>

    suspend fun findRecentlyByEventId(eventId: Long): List<Blooming>

    suspend fun findRecentlyByCafeId(cafeId: Long): List<Blooming>

    suspend fun findRecentBySpotIds(spotIds: List<Long>): List<Blooming>

    suspend fun findRecentByEventIds(eventIds: List<Long>): List<Blooming>

    suspend fun findRecentByCafeIds(cafeIds: List<Long>): List<Blooming>

    fun findTodayBloomingByUserId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming?

    fun findTodayEventBloomingByUserId(
        userId: Long,
        flowerEventId: Long,
    ): Blooming?

    fun findTodayCafeBloomingByUserId(
        userId: Long,
        flowerSpotCafeId: Long,
    ): Blooming?

    fun findBloomedSpotIdsByFlowerSpotIds(spotIds: List<Long>): List<Long>

    fun findBloomedEventIdsByFlowerEventIds(eventIds: List<Long>): List<Long>

    /**
     * 지역별, 상태별 최근 5일간 투표 수를 집계합니다.
     *
     * @return 지역별 상태별 투표 수 리스트
     */
    fun countByRegionAndStatus(): List<RegionStatusCount>

    /**
     * 특정 지역에서 특정 시점 이후 BLOOMED 투표 수를 조회합니다.
     */
    fun countBloomedVotesByRegionAndCreatedAtAfter(
        region: Region,
        createdAtAfter: LocalDateTime,
    ): Long
}
