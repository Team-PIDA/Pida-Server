package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpotCafe
import com.pida.flowerspot.FlowerSpotCafeRepository
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
class FlowerSpotCafeCoreRepository(
    private val flowerSpotCafeJpaRepository: FlowerSpotCafeJpaRepository,
    private val flowerSpotCafeCustomRepository: FlowerSpotCafeCustomRepository,
    private val tx: TransactionTemplates,
) : FlowerSpotCafeRepository {
    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

    override suspend fun findBy(cafeId: Long): FlowerSpotCafe =
        tx.reader.coExecute {
            flowerSpotCafeJpaRepository
                .findByIdAndDeletedAtIsNullOrElseThrow(cafeId)
                .toFlowerSpotCafe()
        }

    override suspend fun findAll(): List<FlowerSpotCafe> =
        tx.reader.coExecute {
            flowerSpotCafeJpaRepository
                .findByDeletedAtIsNullOrderByIdAsc()
                .map { it.toFlowerSpotCafe() }
        }

    override suspend fun findAllByLocation(location: FlowerSpotLocation): List<FlowerSpotCafe> =
        tx.reader.coExecute {
            flowerSpotCafeJpaRepository
                .findWithinBoundsOrderByIdAsc(
                    swLat = location.swLat!!,
                    swLng = location.swLng!!,
                    neLat = location.neLat!!,
                    neLng = location.neLng!!,
                ).map { it.toFlowerSpotCafe() }
        }

    override suspend fun save(cafe: FlowerSpotCafe): FlowerSpotCafe =
        tx.writer.coExecute {
            val point = cafe.pinPoint as GeoJson.Point
            val entity =
                FlowerSpotCafeEntity(
                    name = cafe.name,
                    address = cafe.address,
                    description = cafe.description,
                    thumbnailUrl = cafe.thumbnailUrl,
                    pinPoint = GEOMETRY_FACTORY.createPoint(Coordinate(point.coordinates[0], point.coordinates[1])),
                    region = cafe.region,
                    mapUrl = cafe.mapUrl,
                )
            flowerSpotCafeJpaRepository.save(entity).toFlowerSpotCafe()
        }

    override suspend fun updateThumbnailUrl(
        cafeId: Long,
        thumbnailUrl: String,
    ) {
        tx.writer.coExecute {
            flowerSpotCafeCustomRepository.updateThumbnailUrl(cafeId, thumbnailUrl)
        }
    }
}
