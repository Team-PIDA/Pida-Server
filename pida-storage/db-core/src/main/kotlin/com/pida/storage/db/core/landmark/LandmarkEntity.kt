package com.pida.storage.db.core.landmark

import com.pida.landmark.Landmark
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
@Table(name = "t_landmark")
class LandmarkEntity(
    val name: String,
    @Column(
        name = "name_tsv",
        columnDefinition = "tsvector",
        insertable = false,
        updatable = false,
    )
    val nameTsv: String? = null, // Full Text Search 를 위한 형태소 데이터 타입
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
