package com.pida.storage.db.core.district

import com.pida.place.District
import com.pida.place.DistrictRepository
import com.pida.support.geo.GeoJson
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Repository

@Repository
class DistrictCoreRepository(
    private val districtJpaRepository: DistrictJpaRepository,
) : DistrictRepository {
    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

    override fun saveAll(districts: List<District>) {
        val entities =
            districts.map {
                val point = it.pinPoint as GeoJson.Point
                DistrictEntity(
                    sido = it.sido,
                    sigungu = it.sigungu,
                    eupmyeondonggu = it.eupmyeondonggu,
                    eupmyeonridong = it.eupmyeonridong,
                    ri = it.ri,
                    pinPoint = GEOMETRY_FACTORY.createPoint(Coordinate(point.coordinates[0], point.coordinates[1])),
                )
            }
        districtJpaRepository.saveAll(entities)
    }
}
