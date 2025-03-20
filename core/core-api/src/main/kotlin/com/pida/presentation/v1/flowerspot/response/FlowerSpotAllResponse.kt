package com.pida.presentation.v1.flowerspot.response

import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.GeoJson
import com.pida.flowerspot.Region
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
        description = "핀포인트 정보 (GeoJson)",
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
    @Schema(description = "삭제 여부")
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun from(flowerSpot: FlowerSpot): FlowerSpotResponseDto =
            FlowerSpotResponseDto(
                id = flowerSpot.id,
                address = flowerSpot.address,
                streetName = flowerSpot.streetName,
                district = flowerSpot.district,
                description = flowerSpot.description,
                geom = flowerSpot.geom,
                pinPoint = flowerSpot.pinPoint,
                region = flowerSpot.region,
                deletedAt = flowerSpot.deletedAt,
            )
    }
}
