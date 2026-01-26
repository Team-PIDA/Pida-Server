package com.pida.storage.db.core.landmark

import com.pida.landmark.Landmark
import com.pida.landmark.LandmarkRepository
import com.pida.landmark.NewLandmark
import com.pida.support.tx.TransactionTemplates
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Repository

@Repository
class LandmarkCoreRepository(
    private val landmarkJpaRepository: LandmarkJpaRepository,
    private val tx: TransactionTemplates,
) : LandmarkRepository {
    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

    override fun findAll(): List<Landmark> =
        landmarkJpaRepository
            .findByDeletedAtIsNull()
            .map { it.toLandmark() }

    override fun findByNameContaining(query: String): List<Landmark> =
        landmarkJpaRepository
            .findByNameContainingAndDeletedAtIsNull(query)
            .map { it.toLandmark() }

    override fun saveAll(newLandmarks: List<NewLandmark>) {
        val entities =
            newLandmarks.map {
                LandmarkEntity(
                    name = it.name,
                    address = it.address,
                    pinPoint = GEOMETRY_FACTORY.createPoint(Coordinate(it.x, it.y)),
                    region = it.region,
                )
            }
        landmarkJpaRepository.saveAll(entities)
    }

    override fun existsByName(name: String): Boolean = landmarkJpaRepository.existsByNameAndDeletedAtIsNull(name)
}
