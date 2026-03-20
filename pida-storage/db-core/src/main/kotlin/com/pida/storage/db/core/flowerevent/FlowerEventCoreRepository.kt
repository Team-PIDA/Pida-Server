package com.pida.storage.db.core.flowerevent

import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventRepository
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class FlowerEventCoreRepository(
    private val flowerEventJpaRepository: FlowerEventJpaRepository,
    private val tx: TransactionTemplates,
) : FlowerEventRepository {
    override suspend fun findBy(eventId: Long): FlowerEvent =
        tx.reader.coExecute {
            flowerEventJpaRepository
                .findByIdAndDeletedAtIsNullOrElseThrow(eventId)
                .toFlowerEvent()
        }

    override suspend fun findAllByCategoryId(categoryId: Long): List<FlowerEvent> =
        tx.reader.coExecute {
            flowerEventJpaRepository
                .findByCategoryIdAndDeletedAtIsNullOrderByStartDateAscIdAsc(categoryId)
                .map { it.toFlowerEvent() }
        }

    override suspend fun findAllByCategoryIdAndLocation(
        categoryId: Long,
        location: FlowerSpotLocation,
    ): List<FlowerEvent> =
        tx.reader.coExecute {
            flowerEventJpaRepository
                .findByCategoryIdWithinBoundsOrderByStartDateAscIdAsc(
                    categoryId = categoryId,
                    swLat = location.swLat!!,
                    swLng = location.swLng!!,
                    neLat = location.neLat!!,
                    neLng = location.neLng!!,
                ).map { it.toFlowerEvent() }
        }

    override suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ): List<FlowerEvent> =
        tx.reader.coExecute {
            flowerEventJpaRepository
                .findWithinRadius(latitude, longitude, radiusMeters)
                .map { it.toFlowerEvent() }
        }
}
