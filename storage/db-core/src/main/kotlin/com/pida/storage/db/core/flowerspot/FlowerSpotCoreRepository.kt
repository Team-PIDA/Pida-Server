package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.tx.Tx
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCoreRepository(
    private val flowerSpotJpaRepository: FlowerSpotJpaRepository,
) : FlowerSpotRepository {
    override fun findBy(spotId: Long): FlowerSpot =
        flowerSpotJpaRepository
            .findByIdAndDeletedAtIsNullOrElseThrow(spotId)
            .toFlowerSpot()

    override fun findAll(): List<FlowerSpot> =
        Tx.readable {
            flowerSpotJpaRepository
                .findByDeletedAtIsNull()
                ?.map { it.toFlowerSpot() }
                ?: emptyList()
        }
}
