package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.GeoJson
import com.pida.flowerspot.Region
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

@Entity
@Table(name = "t_flower_spot")
class FlowerSpotEntity(
    val streetName: String,
    val address: String?,
    val district: String?,
    val description: String?,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val region: Region,
    @Column(columnDefinition = "geometry(LineString, 4326)")
    val geom: LineString,
    @Column(columnDefinition = "geometry(Point, 4326)")
    val pinPoint: Point,
) : BaseEntity() {
    fun toFlowerSpot(): FlowerSpot =
        FlowerSpot(
            id = id!!,
            address = address,
            streetName = streetName,
            district = district,
            description = description,
            geom = GeoJson.LineString(geom.coordinates.map { listOf(it.x, it.y) }),
            pinPoint = GeoJson.Point(listOf(pinPoint.x, pinPoint.y)),
            region = region,
            deletedAt = deletedAt,
        )
}
