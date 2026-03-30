package com.pida.presentation.v2.flowerspotcafe.request

import com.pida.flowerspot.NewFlowerSpotCafe
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "꽃 명소 카페 생성 요청")
data class FlowerSpotCafeCreateRequest(
    @Schema(description = "카페 이름", example = "벚꽃 카페")
    val name: String,
    @Schema(description = "주소", example = "서울특별시 영등포구 여의도동", required = false)
    val address: String?,
    @Schema(description = "설명", example = "벚꽃 명소 근처 분위기 좋은 카페", required = false)
    val description: String?,
    @Schema(description = "경도 (longitude)", example = "126.9246")
    val longitude: Double,
    @Schema(description = "위도 (latitude)", example = "37.5284")
    val latitude: Double,
    @Schema(description = "지역", example = "SEOUL")
    val region: Region,
    @Schema(description = "지도 URL", example = "https://map.naver.com/example", required = false)
    val mapUrl: String?,
) {
    init {
        if (!mapUrl.isNullOrBlank()) {
            require(URL_PATTERN.matches(mapUrl)) {
                "Invalid map URL format: $mapUrl"
            }
        }
    }

    fun toNewFlowerSpotCafe(): NewFlowerSpotCafe =
        NewFlowerSpotCafe(
            name = name,
            address = address,
            description = description,
            longitude = longitude,
            latitude = latitude,
            region = region,
            mapUrl = mapUrl,
        )

    companion object {
        private val URL_PATTERN = Regex("^https?://[\\w\\-.]+(:\\d+)?(/\\S*)?$")
    }
}
