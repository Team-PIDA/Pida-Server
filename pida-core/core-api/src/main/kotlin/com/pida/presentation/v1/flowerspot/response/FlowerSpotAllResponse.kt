package com.pida.presentation.v1.flowerspot.response

import com.pida.blooming.BloomingStatus
import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpotDetails
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "벚꽃 장소 목록 응답")
data class FlowerSpotAllResponse(
    @field:ArraySchema(
        schema = Schema(implementation = FlowerSpotResponseDto::class),
        arraySchema = Schema(description = "벚꽃 장소 목록"),
    )
    val list: List<FlowerSpotResponseDto>,
) {
    companion object {
        fun of(list: List<FlowerSpotResponseDto>) = FlowerSpotAllResponse(list)
    }
}

@Schema(description = "벚꽃 장소 응답 DTO")
data class FlowerSpotResponseDto(
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
    val geom: GeoJson,
    @Schema(
        description = "핀 포인트 정보 (GeoJson)",
        example = """
        {
          "type": "Point",
          "coordinates": [127.10317, 37.48881]
        }
    """,
    )
    val pinPoint: GeoJson,
    @Schema(description = "지역", example = "SEOUL")
    val region: Region,
    @Schema(description = "꽃 종류", example = "BLOSSOM")
    val kind: FlowerKind,
    @Schema(
        description = "미리보기 이미지 URL",
        example = "https://example.com/image1.jpg",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val previewUrl: String?,
    @Schema(description = "삭제 여부")
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun from(flowerSpot: FlowerSpotDetails): FlowerSpotResponseDto =
            FlowerSpotResponseDto(
                id = flowerSpot.id,
                address = flowerSpot.address,
                recentlyVisitedCount = flowerSpot.recentlyVisitedCount,
                bloomingStatus = flowerSpot.bloomingStatus,
                streetName = flowerSpot.streetName,
                district = flowerSpot.district,
                description = flowerSpot.description,
                geom = flowerSpot.geom,
                pinPoint = flowerSpot.pinPoint,
                region = flowerSpot.region,
                kind = flowerSpot.kind,
                previewUrl = flowerSpot.imageUrls.firstOrNull(),
                deletedAt = flowerSpot.deletedAt,
            )
    }
}
