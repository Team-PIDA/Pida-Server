package com.pida.user.location

import com.pida.support.geo.GeoJson
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

class UserLocation {
    data class Create(
        val userId: Long,
        val location: Point, // Point GeoJson
    )

    data class Info(
        val id: Long,
        val userId: Long,
        val location: GeoJson, // Point GeoJson
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}
