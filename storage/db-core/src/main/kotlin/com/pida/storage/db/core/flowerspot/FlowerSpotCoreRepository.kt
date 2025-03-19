package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCoreRepository(
    private val flowerSpotJpaRepository: FlowerSpotJpaRepository,
) : FlowerSpotRepository {
    override fun findBy(spotId: Long): FlowerSpot = flowerSpotJpaRepository.findByIdAndDeletedAtIsNullOrElseThrow(spotId).toFlowerSpot()
}
