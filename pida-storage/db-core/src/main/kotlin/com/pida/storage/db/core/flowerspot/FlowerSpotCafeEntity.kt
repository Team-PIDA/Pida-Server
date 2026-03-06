package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpotCafe
import com.pida.storage.db.core.support.BaseEntity
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point

@Entity
@Table(name = "t_flower_spot_cafe")
class FlowerSpotCafeEntity(
    val flowerSpotId: Long,
    val name: String,
    val address: String?,
    val description: String?,
    @Column(columnDefinition = "geometry(Point, 4326)")
    val pinPoint: Point,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val region: Region,
    val mapUrl: String?,
) : BaseEntity() {
    fun toFlowerSpotCafe(): FlowerSpotCafe =
        FlowerSpotCafe(
            id = id!!,
            flowerSpotId = flowerSpotId,
            name = name,
            address = address,
            description = description,
            pinPoint = GeoJson.Point(listOf(pinPoint.x, pinPoint.y)),
            region = region,
            mapUrl = mapUrl,
            deletedAt = deletedAt,
        )
}
