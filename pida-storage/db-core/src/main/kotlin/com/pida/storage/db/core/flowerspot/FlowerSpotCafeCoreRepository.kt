package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpotCafe
import com.pida.flowerspot.FlowerSpotCafeRepository
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCafeCoreRepository(
    private val flowerSpotCafeJpaRepository: FlowerSpotCafeJpaRepository,
    private val tx: TransactionTemplates,
) : FlowerSpotCafeRepository {
    override suspend fun findBy(cafeId: Long): FlowerSpotCafe =
        tx.reader.coExecute {
            flowerSpotCafeJpaRepository
                .findByIdAndDeletedAtIsNullOrElseThrow(cafeId)
                .toFlowerSpotCafe()
        }

    override suspend fun findAllByFlowerSpotId(flowerSpotId: Long): List<FlowerSpotCafe> =
        tx.reader.coExecute {
            flowerSpotCafeJpaRepository
                .findByFlowerSpotIdAndDeletedAtIsNull(flowerSpotId)
                .map { it.toFlowerSpotCafe() }
        }
}
