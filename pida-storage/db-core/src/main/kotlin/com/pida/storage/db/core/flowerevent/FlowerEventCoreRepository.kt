package com.pida.storage.db.core.flowerevent

import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventRepository
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.geo.GeoJson
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Repository

@Repository
class FlowerEventCoreRepository(
    private val flowerEventJpaRepository: FlowerEventJpaRepository,
    private val flowerEventCustomRepository: FlowerEventCustomRepository,
    private val tx: TransactionTemplates,
) : FlowerEventRepository {
    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

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

    override suspend fun save(event: FlowerEvent): FlowerEvent =
        tx.writer.coExecute {
            val point = event.pinPoint as GeoJson.Point
            val entity =
                FlowerEventEntity(
                    name = event.name,
                    address = event.address,
                    thumbnailUrl = event.thumbnailUrl,
                    pinPoint = GEOMETRY_FACTORY.createPoint(Coordinate(point.coordinates[0], point.coordinates[1])),
                    region = event.region,
                    homepageUrl = event.homepageUrl,
                    startDate = event.startDate,
                    endDate = event.endDate,
                    categoryId = event.categoryId,
                )
            flowerEventJpaRepository.save(entity).toFlowerEvent()
        }

    override suspend fun updateThumbnailUrl(
        eventId: Long,
        thumbnailUrl: String,
    ) {
        tx.writer.coExecute {
            flowerEventCustomRepository.updateThumbnailUrl(eventId, thumbnailUrl)
        }
    }
}
