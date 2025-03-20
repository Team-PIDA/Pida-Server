package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.tx.TxAdvice
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCoreRepository(
    private val flowerSpotJpaRepository: FlowerSpotJpaRepository,
    private val txAdvice: TxAdvice,
) : FlowerSpotRepository {
    override fun findBy(spotId: Long): FlowerSpot =
        flowerSpotJpaRepository
            .findByIdAndDeletedAtIsNullOrElseThrow(spotId)
            .toFlowerSpot()

    override suspend fun findAll(): List<FlowerSpot> =
        txAdvice.coReadOnly {
            flowerSpotJpaRepository
                .findByDeletedAtIsNull()
                .map { it.toFlowerSpot() }
        }
}
