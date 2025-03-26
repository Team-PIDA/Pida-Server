package com.pida.presentation.v1.flowerspot.response

import com.pida.blooming.BloomingStatus
import com.pida.flowerspot.FlowerSpotDetails
import com.pida.flowerspot.GeoJson
import com.pida.flowerspot.Region
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class FlowerSpotDetailsResponse(
    @Schema(description = "장소 ID", example = "1")
    val id: Long,
    @Schema(description = "주소", example = "서울특별시 강남구 수서동")
    val address: String?,
    @Schema(description = "최근 방문 횟수", example = "5")
    val recentlyVisitedCount: Long,
    @Schema(description = "개화 상태", example = "BLOOMED")
    val bloomingStatus: BloomingStatus,
    @Schema(description = "도로명", example = "밤고개1길")
    val streetName: String,
    @Schema(description = "행정동", example = "수서동")
    val district: String?,
    @Schema(description = "설명", example = "벚꽃이 예쁜 길")
    val description: String?,
    @Schema(
        description = "라인 정보 (GeoJson)",
        example = """
        {
          "type": "LineString",
          "coordinates": [
            [127.10079, 37.48809],
            [127.10116, 37.48825],
            [127.10221, 37.48852],
            [127.10534, 37.48943]
          ]
        }
    """,
    )
    val geom: GeoJson, // LineString GeoJson
    @Schema(
        description = "핀포인트 정보 (GeoJson)",
        example = """
        {
          "type": "Point",
          "coordinates": [127.10317, 37.48881]
        }
    """,
    )
    val pinPoint: GeoJson, // Point GeoJson
    @Schema(description = "지역", example = "SEOUL")
    val region: Region,
    @Schema(description = "삭제 일자", example = "2025-04-01T00:00:00")
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun of(flowerSpotDetails: FlowerSpotDetails) =
            FlowerSpotDetailsResponse(
                id = flowerSpotDetails.id,
                address = flowerSpotDetails.address,
                recentlyVisitedCount = flowerSpotDetails.recentlyVisitedCount,
                bloomingStatus = flowerSpotDetails.bloomingStatus,
                streetName = flowerSpotDetails.streetName,
                district = flowerSpotDetails.district,
                description = flowerSpotDetails.description,
                geom = flowerSpotDetails.geom,
                pinPoint = flowerSpotDetails.pinPoint,
                region = flowerSpotDetails.region,
                deletedAt = flowerSpotDetails.deletedAt,
            )
    }
}
