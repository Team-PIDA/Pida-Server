package com.pida.storage.db.core.district

import com.pida.place.District
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
@Table(name = "t_district")
class DistrictEntity(
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val sido: Region,
    val sigungu: String,
    val eupmyeondonggu: String?,
    val eupmyeonridong: String?,
    val ri: String?,
    @Column(columnDefinition = "geometry(Point, 4326)")
    val pinPoint: Point,
) : BaseEntity() {
    fun toDistrict(): District =
        District(
            id = id!!,
            sido = sido,
            sigungu = sigungu,
            eupmyeondonggu = eupmyeondonggu,
            eupmyeonridong = eupmyeonridong,
            ri = ri,
            pinPoint = GeoJson.Point(listOf(pinPoint.x, pinPoint.y)),
        )
}
