package com.pida.notification.withered

import com.pida.blooming.BloomingRepository
import com.pida.blooming.BloomingStatus
import org.springframework.stereotype.Component

/**
 * 지역별 WITHERED 상태 투표 비율을 확인하여 임계값을 초과한 지역을 찾는 컴포넌트
 *
 * 최근 5일간의 투표 데이터를 기준으로 WITHERED 비율이 30% 이상인 지역을 반환합니다.
 */
@Component
class WitheredThresholdChecker(
    private val bloomingRepository: BloomingRepository,
) {
    companion object {
        private const val THRESHOLD_PERCENTAGE = 30.0
    }

    /**
     * WITHERED 투표 비율이 30% 이상인 지역을 찾습니다.
     *
     * @return 임계값을 초과한 지역 리스트
     */
    fun findRegionsExceedingThreshold(): List<WitheredRegion> {
        val regionStats = bloomingRepository.countByRegionAndStatus()

        if (regionStats.isEmpty()) {
            return emptyList()
        }

        val result =
            regionStats
                .groupBy { it.region }
                .mapNotNull { (region, counts) ->
                    val totalVotes = counts.sumOf { it.count }

                    if (totalVotes == 0L) {
                        return@mapNotNull null
                    }

                    val witheredVotes =
                        counts
                            .filter { it.status == BloomingStatus.WITHERED }
                            .sumOf { it.count }

                    val percentage = (witheredVotes.toDouble() / totalVotes) * 100

                    if (percentage >= THRESHOLD_PERCENTAGE) {
                        WitheredRegion(
                            region = region,
                            witheredPercentage = percentage,
                            totalVotes = totalVotes.toInt(),
                            witheredVotes = witheredVotes.toInt(),
                        )
                    } else {
                        null
                    }
                }

        return result
    }
}
