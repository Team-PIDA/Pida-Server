package com.pida.flowerspot

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingStatus
import com.pida.support.aws.S3ImageInfo
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.time.LocalDateTime

data class FlowerSpotDetails(
    val id: Long,
    val address: String?,
    val recentlyVisitedCount: Long,
    val bloomingStatus: BloomingStatus,
    val streetName: String,
    val district: String?,
    val description: String?,
    val geom: GeoJson, // LineString GeoJson
    val pinPoint: GeoJson, // Point GeoJson
    val region: Region,
    val kind: FlowerKind,
    val images: List<FlowerSpotImage> = emptyList(),
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun of(
            flowerSpot: FlowerSpot,
            bloomings: List<Blooming>,
            images: List<S3ImageInfo> = emptyList(),
        ) = FlowerSpotDetails(
            id = flowerSpot.id,
            address = flowerSpot.address,
            recentlyVisitedCount = bloomings.size.toLong(),
            bloomingStatus = bloomings.groupBy { it.status }.maxByOrNull { it.value.size }?.key ?: BloomingStatus.NOT_BLOOMED,
            streetName = flowerSpot.streetName,
            district = flowerSpot.district,
            description = flowerSpot.description,
            geom = flowerSpot.geom,
            pinPoint = flowerSpot.pinPoint,
            region = flowerSpot.region,
            kind = flowerSpot.kind,
            images = images.map { FlowerSpotImage(url = it.url, createdAt = it.uploadedAt) },
            deletedAt = flowerSpot.deletedAt,
        )
    }
}
