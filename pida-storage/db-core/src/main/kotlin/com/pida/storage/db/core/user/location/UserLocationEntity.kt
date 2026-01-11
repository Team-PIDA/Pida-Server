package com.pida.storage.db.core.user.location

import com.pida.flowerspot.GeoJson
import com.pida.storage.db.core.support.BaseEntity
import com.pida.user.location.UserLocation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point

@Table(name = "t_user_location")
@Entity
class UserLocationEntity(
    val userId: Long,
    @Column(columnDefinition = "geometry(Point, 4326)")
    var location: Point,
) : BaseEntity() {
    constructor(
        create: UserLocation.Create,
    ) : this(
        userId = create.userId,
        location = create.location,
    )

    fun updateLocation(newLocation: Point) {
        this.location = newLocation
    }

    fun toUserLocation(): UserLocation.Info =
        UserLocation.Info(
            id = id!!,
            userId = userId,
            location = GeoJson.Point(listOf(location.x, location.y)),
            createdAt = createdAt,
            updatedAt = updatedAt!!,
        )
}
