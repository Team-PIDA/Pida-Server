package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCoreRepository(
    private val flowerSpotJpaRepository: FlowerSpotJpaRepository,
    private val tx: TransactionTemplates,
) : FlowerSpotRepository {
    override fun findBy(spotId: Long): FlowerSpot =
        flowerSpotJpaRepository
            .findByIdAndDeletedAtIsNullOrElseThrow(spotId)
            .toFlowerSpot()

    override suspend fun findAll(): List<FlowerSpot> =
        tx.reader.coExecute {
            flowerSpotJpaRepository
                .findByDeletedAtIsNull()
                .map { it.toFlowerSpot() }
        }
}
