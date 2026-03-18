package com.pida.storage.db.core.flowerevent

import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventRepository
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.support.tx.Tx
import org.springframework.stereotype.Repository

@Repository
class FlowerEventCoreRepository(
    private val flowerEventJpaRepository: FlowerEventJpaRepository,
) : FlowerEventRepository {
    override suspend fun findAllByCategoryId(categoryId: Long): List<FlowerEvent> =
        Tx.coReadable {
            flowerEventJpaRepository
                .findByCategoryIdAndDeletedAtIsNullOrderByStartDateAscIdAsc(categoryId)
                .map { it.toFlowerEvent() }
        }

    override suspend fun findAllByCategoryIdAndLocation(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<FlowerEvent> =
        Tx.coReadable {
            flowerEventJpaRepository
                .findByCategoryIdWithinBoundsOrderByStartDateAscIdAsc(
                    categoryId = categoryId,
                    swLat = location.swLat!!,
                    swLng = location.swLng!!,
                    neLat = location.neLat!!,
                    neLng = location.neLng!!,
                ).map { it.toFlowerEvent() }
        }
}
