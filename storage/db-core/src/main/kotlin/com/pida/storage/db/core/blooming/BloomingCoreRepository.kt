package com.pida.storage.db.core.blooming

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingRepository
import com.pida.blooming.NewBlooming
import com.pida.support.tx.TxAdvice
import org.springframework.stereotype.Repository

@Repository
class BloomingCoreRepository(
    private val bloomingJpaRepository: BloomingJpaRepository,
    private val txAdvice: TxAdvice,
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

    override suspend fun findByUserIdAndSpotId(
        userId: Long,
        flowerSpotId: Long,
    ): Blooming? =
        txAdvice.readOnly {
            bloomingJpaRepository.findByUserIdAndFlowerSpotId(userId, flowerSpotId)?.toBlooming()
        }

    override suspend fun findAllByUserId(userId: Long): List<Blooming> =
        txAdvice.readOnly {
            bloomingJpaRepository.findAllByUserId(userId).map { it.toBlooming() }
        }

    override suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<Blooming> =
        txAdvice.readOnly {
            bloomingJpaRepository.findAllByFlowerSpotId(flowerSpotId).map { it.toBlooming() }
        }
}
