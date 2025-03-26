package com.pida.storage.db.core.blooming

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingRepository
import com.pida.blooming.NewBlooming
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.TxAdvice
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class BloomingCoreRepository(
    private val bloomingJpaRepository: BloomingJpaRepository,
    private val bloomingCustomRepository: BloomingCustomRepository,
    private val txAdvice: TxAdvice,
    private val tx: TransactionTemplates,
) : BloomingRepository {
    override fun add(newBlooming: NewBlooming): Blooming =
        txAdvice.write {
            val bloomingEntity =
                BloomingEntity(
                    userId = newBlooming.userId,
                    flowerSpotId = newBlooming.flowerSpotId,
                    status = newBlooming.status,
                )
            bloomingJpaRepository.save(bloomingEntity).toBlooming()
        }

    override suspend fun findTopByUserIdAndSpotIdDecs(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? =
        txAdvice.readOnly {
            bloomingJpaRepository.findTopByUserIdAndFlowerSpotIdOrderByCreatedAtDesc(userId, flowerSpotId)?.toBlooming()
        }

    override suspend fun findAllByUserId(userId: Long): List<Blooming> =
        txAdvice.readOnly {
            bloomingJpaRepository.findAllByUserId(userId).map { it.toBlooming() }
        }

    override suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming> =
        txAdvice.readOnly {
            bloomingJpaRepository.findAllByFlowerSpotId(flowerSpotId).map { it.toBlooming() }
        }

    override suspend fun findRecentlyBySpotId(spotId: Long): List<Blooming> =
        tx.reader.coExecute {
            bloomingCustomRepository.recentlyBySpotId(spotId).map { it.toBlooming() }
        }

    override fun findRecentBySpotIds(spotIds: List<Long>): List<Blooming> =
        txAdvice.readOnly {
            bloomingCustomRepository.recentlyBySpotIds(spotIds).map { it.toBlooming() }
        }
}
