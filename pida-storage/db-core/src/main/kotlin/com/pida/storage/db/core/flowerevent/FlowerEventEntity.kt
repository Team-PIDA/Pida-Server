package com.pida.storage.db.core.flowerevent

import com.pida.flowerevent.FlowerEvent
import com.pida.storage.db.core.support.BaseEntity
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point
import java.time.LocalDate

@Entity
@Table(name = "t_flower_event")
class FlowerEventEntity(
    val name: String,
    val address: String?,
    @Column(columnDefinition = "geometry(Point, 4326)")
    val pinPoint: Point,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val region: Region,
    val homepageUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val categoryId: Long,
) : BaseEntity() {
    fun toFlowerEvent(): FlowerEvent =
        FlowerEvent(
            id = id!!,
            name = name,
            address = address,
            pinPoint = GeoJson.Point(listOf(pinPoint.x, pinPoint.y)),
            region = region,
            homepageUrl = homepageUrl,
            startDate = startDate,
            endDate = endDate,
            categoryId = categoryId,
            deletedAt = deletedAt,
        )
}
