package com.pida.notification.bloomed

import com.pida.blooming.BloomingRepository
import com.pida.blooming.BloomingStatus
import org.springframework.stereotype.Component

/**
 * 지역별 BLOOMED 상태 투표 비율을 확인하여 임계값을 초과한 지역을 찾는 컴포넌트
 *
 * 최근 5일간의 투표 데이터를 기준으로 BLOOMED 비율이 80% 이상인 지역을 반환합니다.
 */
@Component
class BloomedThresholdChecker(
    private val bloomingRepository: BloomingRepository,
) {
    companion object {
        private const val THRESHOLD_PERCENTAGE = 80.0
    }

    /**
     * BLOOMED 투표 비율이 80% 이상인 지역을 찾습니다.
     *
     * @return 임계값을 초과한 지역 리스트
     */
    fun findRegionsExceedingThreshold(): List<BloomedRegion> {
        val regionStats = bloomingRepository.countByRegionAndStatus()

        if (regionStats.isEmpty()) {
            return emptyList()
        }

        return regionStats
            .groupBy { it.region }
            .mapNotNull { (region, counts) ->
                val totalVotes = counts.sumOf { it.count }

                if (totalVotes == 0L) {
                    return@mapNotNull null
                }

                val bloomedVotes =
                    counts
                        .filter { it.status == BloomingStatus.BLOOMED }
                        .sumOf { it.count }

                val percentage = (bloomedVotes.toDouble() / totalVotes) * 100

                if (percentage >= THRESHOLD_PERCENTAGE) {
                    BloomedRegion(
                        region = region,
                        bloomedPercentage = percentage,
                        totalVotes = totalVotes.toInt(),
                        bloomedVotes = bloomedVotes.toInt(),
                    )
                } else {
                    null
                }
            }
    }
}
