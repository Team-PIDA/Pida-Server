package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotLocation
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.geo.Region
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class FlowerSpotCoreRepository(
    private val flowerSpotJpaRepository: FlowerSpotJpaRepository,
    private val tx: TransactionTemplates,
) : FlowerSpotRepository {
    override suspend fun findBy(spotId: Long): FlowerSpot =
        tx.reader.coExecute {
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

    override suspend fun findAllByLocation(location: FlowerSpotLocation): List<FlowerSpot> =
        tx.reader.coExecute {
            flowerSpotJpaRepository
                .findWithinBounds(
                    location.swLat!!,
                    location.swLng!!,
                    location.neLat!!,
                    location.neLng!!,
                ).map { it.toFlowerSpot() }
        }

    override suspend fun findAllByLocationAndRegion(
        region: Region,
        location: FlowerSpotLocation,
    ): List<FlowerSpot> =
        tx.reader.coExecute {
            flowerSpotJpaRepository
                .findWithinBoundsAndRegion(
                    location.swLat!!,
                    location.swLng!!,
                    location.neLat!!,
                    location.neLng!!,
                    region.name,
                ).map { it.toFlowerSpot() }
        }

    override suspend fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ): List<FlowerSpot> =
        tx.reader.coExecute {
            flowerSpotJpaRepository
                .findWithinRadius(latitude, longitude, radiusMeters)
                .map { it.toFlowerSpot() }
        }

    override fun findByStreetNameContaining(streetName: String): List<FlowerSpot> =
        flowerSpotJpaRepository
            .findByStreetNameContainingAndDeletedAtIsNull(streetName)
            .map { it.toFlowerSpot() }

    override suspend fun updatePreviewImageKey(
        spotId: Long,
        key: String,
        uploadedAt: LocalDateTime,
    ) = tx.writer.coExecute {
        val entity = flowerSpotJpaRepository.findByIdAndDeletedAtIsNullOrElseThrow(spotId)
        entity.updatePreviewImage(key, uploadedAt)
    }
}
