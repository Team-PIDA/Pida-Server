package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.GeoJson
import com.pida.flowerspot.Landmark
import com.pida.flowerspot.Region
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point

@Entity
@Table(name = "t_landmark")
class LandmarkEntity(
    val name: String,
    val address: String?,
    @Column(columnDefinition = "geometry(Point, 4326)")
    val pinPoint: Point,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val region: Region,
) : BaseEntity() {
    fun toLandmark(): Landmark =
        Landmark(
            id = id!!,
            name = name,
            address = address,
            pinPoint = GeoJson.Point(listOf(pinPoint.x, pinPoint.y)),
            region = region,
            deletedAt = deletedAt,
        )
}
