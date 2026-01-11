package com.pida.user.location

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Component

@Component
class UserLocationAppender(
    private val userLocationRepository: UserLocationRepository,
) {
    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

    fun appendUserLocation(
        userId: Long,
        latitude: Double,
        longitude: Double,
    ): UserLocation.Info {
        val point = GEOMETRY_FACTORY.createPoint(Coordinate(longitude, latitude))
        return userLocationRepository.saveOrUpdate(userId, latitude, longitude)
    }
}
