package com.pida.reporter

import org.springframework.stereotype.Component

@Component
class RecentReporterFinder(
    private val recentReporterRepository: RecentReporterRepository,
) {
    suspend fun findRecentlyByFlowerSpotId(flowerSpotId: Long): RecentReporter? =
        recentReporterRepository.findRecentlyByFlowerSpotId(flowerSpotId)
}
