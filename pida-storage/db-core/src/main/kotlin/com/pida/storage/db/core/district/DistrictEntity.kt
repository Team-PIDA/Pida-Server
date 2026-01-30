package com.pida.storage.db.core.district

import com.pida.place.District
import com.pida.storage.db.core.support.BaseEntity
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point

@Entity
@Table(
    name = "t_district",
    indexes = [
        Index(name = "idx_district_sido", columnList = "sido"),
        Index(name = "idx_district_sigungu", columnList = "sigungu"),
        Index(name = "idx_district_eupmyeondonggu", columnList = "eupmyeondonggu"),
        Index(name = "idx_district_eupmyeonridong", columnList = "eupmyeonridong"),
        Index(name = "idx_district_ri", columnList = "ri"),
    ],
)
class DistrictEntity(
    val sido: String,
    val sigungu: String?,
    val eupmyeondonggu: String?,
    val eupmyeonridong: String?,
    val ri: String?,
    @Column(columnDefinition = "geometry(Point, 4326)")
    val pinPoint: Point,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val region: Region,
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
            region = region,
        )
}
