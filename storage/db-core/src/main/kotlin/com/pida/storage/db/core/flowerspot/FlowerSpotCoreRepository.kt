package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.flowerspot.Region
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.TxAdvice
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCoreRepository(
    private val flowerSpotJpaRepository: FlowerSpotJpaRepository,
    private val tx: TransactionTemplates,
    private val txAdvice: TxAdvice,
) : FlowerSpotRepository {
    override suspend fun findBy(spotId: Long): FlowerSpot =
        txAdvice.readOnly {
            flowerSpotJpaRepository
                .findByIdAndDeletedAtIsNullOrElseThrow(spotId)
                .toFlowerSpot()
        }

    override suspend fun findAll(): List<FlowerSpot> =
        tx.reader.coExecute {
            flowerSpotJpaRepository
                .findByDeletedAtIsNull()
                .map { it.toFlowerSpot() }
        }

    override suspend fun findAllByRegion(region: Region): List<FlowerSpot> =
        tx.reader.coExecute {
            flowerSpotJpaRepository
                .findByRegionAndDeletedAtIsNull(region)
                .map { it.toFlowerSpot() }
        }
}
