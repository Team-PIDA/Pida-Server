package com.pida.presentation.v1.place.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.pida.flowerspot.FlowerSpot
import com.pida.place.District
import com.pida.place.Landmark
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "랜드마크 및 벚꽃길 검색 응답")
data class PlaceSearchResultResponse(
    @field:ArraySchema(
        schema = Schema(implementation = PlaceSearchResponse::class),
        arraySchema = Schema(description = "행정구역 목록"),
    )
    val district: List<PlaceSearchResponse>,
    @field:ArraySchema(
        schema = Schema(implementation = PlaceSearchResponse::class),
        arraySchema = Schema(description = "랜드마크 목록"),
    )
    val landmarks: List<PlaceSearchResponse>,
    @field:ArraySchema(
        schema = Schema(implementation = PlaceSearchResponse::class),
        arraySchema = Schema(description = "벚꽃길 목록"),
    )
    val flowerSpots: List<PlaceSearchResponse>,
) {
    companion object {
        fun of(
            district: List<PlaceSearchResponse>,
            landmarks: List<PlaceSearchResponse>,
            flowerSpots: List<PlaceSearchResponse>,
        ) = PlaceSearchResultResponse(district, landmarks, flowerSpots)
    }
}

@Schema(description = "공통 장소 응답")
data class PlaceSearchResponse(
    @field:Schema(description = "장소 이름", example = "여의도 한강공원", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,
    @field:Schema(description = "주소", example = "서울특별시 영등포구 여의도동", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String?,
    @field:Schema(
        description = "핀 포인트 정보 (GeoJson)",
        example = """
        {
          "type": "Point",
          "coordinates": [126.9340, 37.5284]
        }
    """,
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val pinPoint: GeoJson,
    @field:Schema(description = "지역", example = "SEOUL", requiredMode = Schema.RequiredMode.REQUIRED)
    val region: Region,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Schema(
        description = "벚꽃길 ID (벚꽃길인 경우에만 포함)",
        example = "1",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
    )
    val flowerSpotId: Long? = null,
) {
    companion object {
        fun from(landmark: Landmark) =
            PlaceSearchResponse(
                name = landmark.name,
                address = landmark.address,
                pinPoint = landmark.pinPoint,
                region = landmark.region,
            )

        fun from(flowerSpot: FlowerSpot) =
            PlaceSearchResponse(
                name = flowerSpot.streetName,
                address = flowerSpot.address,
                pinPoint = flowerSpot.pinPoint,
                region = flowerSpot.region,
                flowerSpotId = flowerSpot.id,
            )

        fun from(district: District) =
            PlaceSearchResponse(
                name =
                    listOfNotNull(
                        district.sido,
                        district.sigungu,
                        district.eupmyeondonggu,
                        district.eupmyeonridong,
                        district.ri,
                    ).last(),
                address =
                    listOfNotNull(district.sigungu, district.eupmyeondonggu, district.eupmyeonridong, district.ri)
                        .takeIf { it.isNotEmpty() }
                        ?.let { listOf(district.sido) + it }
                        ?.joinToString(" "),
                pinPoint = district.pinPoint,
                region = district.region,
            )
    }
}
