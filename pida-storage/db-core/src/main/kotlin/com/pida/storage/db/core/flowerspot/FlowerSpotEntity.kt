package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotType
import com.pida.storage.db.core.support.BaseEntity
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

@Entity
@Table(
    name = "t_flower_spot",
    indexes = [
        Index(name = "idx_flower_spot_region_deleted_at", columnList = "region, deleted_at"),
    ],
)
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
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(30)")
    val kind: FlowerKind,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(30)")
    val type: FlowerSpotType,
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
            kind = kind,
            type = type,
            deletedAt = deletedAt,
        )
}
