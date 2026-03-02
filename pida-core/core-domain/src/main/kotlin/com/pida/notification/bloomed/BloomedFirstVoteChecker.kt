package com.pida.notification.bloomed

import com.pida.blooming.BloomingRepository
import com.pida.support.geo.Region
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 해당 연도 기준 지역별 첫 BLOOMED 투표 여부를 판별합니다.
 */
@Component
class BloomedFirstVoteChecker(
    private val bloomingRepository: BloomingRepository,
) {
    fun isFirstBloomedVoteOfYear(region: Region): Boolean {
        val yearStartDateTime = getYearStartDateTime()
        val bloomedVoteCount =
            bloomingRepository.countBloomedVotesByRegionAndCreatedAtAfter(
                region = region,
                createdAtAfter = yearStartDateTime,
            )

        return bloomedVoteCount == 1L
    }

    private fun getYearStartDateTime() =
        LocalDate
            .now()
            .withDayOfYear(1)
            .atStartOfDay()
}
