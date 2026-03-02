package com.pida.storage.db.core.blooming

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingRepository
import com.pida.blooming.NewBlooming
import com.pida.blooming.RegionStatusCount
import com.pida.support.geo.Region
import com.pida.support.tx.Tx
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BloomingCoreRepository(
    private val bloomingJpaRepository: BloomingJpaRepository,
    private val bloomingCustomRepository: BloomingCustomRepository,
) : BloomingRepository {
    override fun add(newBlooming: NewBlooming): Blooming =
        Tx.writeable {
            val bloomingEntity =
                BloomingEntity(
                    userId = newBlooming.userId,
                    flowerSpotId = newBlooming.flowerSpotId,
                    status = newBlooming.status,
                )
            bloomingJpaRepository.save(bloomingEntity).toBlooming()
        }

    override suspend fun findTopByUserIdAndSpotIdDesc(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? =
        Tx.readable {
            bloomingJpaRepository.findTopByUserIdAndFlowerSpotIdOrderByCreatedAtDesc(userId, flowerSpotId)?.toBlooming()
        }

    override suspend fun findAllByUserId(userId: Long): List<Blooming> =
        Tx.readable {
            bloomingJpaRepository.findAllByUserId(userId).map { it.toBlooming() }
        }

    override suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming> =
        Tx.readable {
            bloomingJpaRepository.findAllByFlowerSpotId(flowerSpotId).map { it.toBlooming() }
        }

    override suspend fun findRecentlyBySpotId(spotId: Long): List<Blooming> =
        Tx.coReadable {
            bloomingCustomRepository.recentlyBySpotId(spotId).map { it.toBlooming() }
        }

    override fun findRecentBySpotIds(spotIds: List<Long>): List<Blooming> =
        Tx.readable {
            bloomingCustomRepository.recentlyBySpotIds(spotIds).map { it.toBlooming() }
        }

    override fun findTodayBloomingByUserId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? =
        Tx.readable {
            bloomingCustomRepository.findTodayBloomingByUserId(userId, flowerSpotId)?.toBlooming()
        }

    override fun findBloomedSpotIdsByFlowerSpotIds(spotIds: List<Long>): List<Long> =
        Tx.readable {
            bloomingCustomRepository.findBloomedSpotIdsByFlowerSpotIds(spotIds)
        }

    override fun countByRegionAndStatus(): List<RegionStatusCount> =
        Tx.readable {
            bloomingCustomRepository.countByRegionAndStatus()
        }

    override fun countBloomedVotesByRegionAndCreatedAtAfter(
        region: Region,
        createdAtAfter: LocalDateTime,
    ): Long =
        Tx.readable {
            bloomingCustomRepository.countBloomedVotesByRegionAndCreatedAtAfter(
                region = region,
                createdAtAfter = createdAtAfter,
            )
        }
}
