package com.pida.storage.db.core.landmark

import com.pida.place.Landmark
import com.pida.place.LandmarkRepository
import com.pida.place.NewLandmark
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Repository

@Repository
class LandmarkCoreRepository(
    private val landmarkJpaRepository: LandmarkJpaRepository,
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
                    category = it.category,
                )
            }
        landmarkJpaRepository.saveAll(entities)
    }

    override fun existsByName(name: String): Boolean = landmarkJpaRepository.existsByNameAndDeletedAtIsNull(name)

    override fun updateNameTsv() = landmarkJpaRepository.updateNameTsv()
}
