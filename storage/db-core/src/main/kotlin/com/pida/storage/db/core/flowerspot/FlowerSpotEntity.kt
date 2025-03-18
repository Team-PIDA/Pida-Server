package com.pida.storage.db.core.flowerspot

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.Region
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "t_flower_spot")
class FlowerSpotEntity(
    val latitude: String,
    val longitude: String,
    val address: String,
    val streetName: String,
    val district: String?,
    val description: String?,
    val pinPoint: String,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val region: Region,
) : BaseEntity() {
    fun toFlowerSpot(): FlowerSpot =
        FlowerSpot(
            id = id!!,
            latitude = latitude,
            longitude = longitude,
            address = address,
            streetName = streetName,
            district = district,
            description = description,
            pinPoint = pinPoint,
            region = region,
            deletedAt = deletedAt,
        )
}
