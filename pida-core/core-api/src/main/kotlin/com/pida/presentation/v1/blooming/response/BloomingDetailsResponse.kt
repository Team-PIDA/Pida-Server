package com.pida.presentation.v1.blooming.response

import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingStatusDetails
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "개화 상태 상세 조회 응답 Json")
data class BloomingDetailsResponse(
    @Schema(description = "최근 방문한 개화 상태 수", example = "9")
    val totalCount: Long,
    @Schema(
        description = "개화 상태 상세 정보",
        example = """
    {
        "2025-03-27": {
            "BLOOMED": {
                "peopleCount": 1,
                "percentage": 100
            }
        },
        "2025-03-26": {
            "BLOOMED": {
                "peopleCount": 1,
                "percentage": 100
            }
        },
        "2025-03-25": {
            "BLOOMED": {
                "peopleCount": 1,
                "percentage": 50
            },
            "WITHERED": {
                "peopleCount": 1,
                "percentage": 50
            }
        },
        "2025-03-24": {
            "BLOOMED": {
                "peopleCount": 2,
                "percentage": 100
            }
        },
        "2025-03-23": {
            "BLOOMED": {
                "peopleCount": 3,
                "percentage": 100
            }
        }
    }
    """,
    )
    val details: Map<String, Map<String, BloomingStatusDetails>>,
    @Schema(description = "최근 제보자 닉네임", example = "피다")
    val nickname: String? = null,
    @Schema(description = "최근 제보한 날짜", example = "2025-04-10")
    val updatedAt: LocalDate? = null,
) {
    companion object {
        fun from(bloomingDetails: BloomingDetails): BloomingDetailsResponse =
            BloomingDetailsResponse(
                totalCount = bloomingDetails.totalCount,
                details = bloomingDetails.details,
                nickname = bloomingDetails.nickname,
                updatedAt = bloomingDetails.updatedAt?.toLocalDate(),
            )
    }
}
