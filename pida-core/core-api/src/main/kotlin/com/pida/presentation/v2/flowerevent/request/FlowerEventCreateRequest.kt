package com.pida.presentation.v2.flowerevent.request

import com.pida.flowerevent.NewFlowerEvent
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "꽃 이벤트 생성 요청")
data class FlowerEventCreateRequest(
    @Schema(description = "이벤트 이름", example = "여의도 벚꽃축제")
    val name: String,
    @Schema(description = "주소", example = "서울특별시 영등포구 여의도동", required = false)
    val address: String?,
    @Schema(description = "경도 (longitude)", example = "126.9246")
    val longitude: Double,
    @Schema(description = "위도 (latitude)", example = "37.5284")
    val latitude: Double,
    @Schema(description = "지역", example = "SEOUL")
    val region: Region,
    @Schema(description = "홈페이지 URL", example = "https://example.com", required = false)
    val homepageUrl: String?,
    @Schema(description = "시작일", example = "2026-04-01")
    val startDate: LocalDate,
    @Schema(description = "종료일", example = "2026-04-07")
    val endDate: LocalDate,
    @Schema(description = "카테고리 ID", example = "1")
    val categoryId: Long,
) {
    init {
        if (!homepageUrl.isNullOrBlank()) {
            require(URL_PATTERN.matches(homepageUrl)) {
                "Invalid homepage URL format: $homepageUrl"
            }
        }
    }

    fun toNewFlowerEvent(): NewFlowerEvent =
        NewFlowerEvent(
            name = name,
            address = address,
            longitude = longitude,
            latitude = latitude,
            region = region,
            homepageUrl = homepageUrl,
            startDate = startDate,
            endDate = endDate,
            categoryId = categoryId,
        )

    companion object {
        private val URL_PATTERN = Regex("^https?://[\\w\\-.]+(:\\d+)?(/\\S*)?$")
    }
}
