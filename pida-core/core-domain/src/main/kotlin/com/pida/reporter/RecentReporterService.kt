package com.pida.reporter

import org.springframework.stereotype.Service

@Service
class RecentReporterService(
    private val recentReporterFinder: RecentReporterFinder,
) {
    suspend fun findRecentlyByFlowerSpotId(flowerSpotId: Long): RecentReporter? =
        recentReporterFinder.findRecentlyByFlowerSpotId(flowerSpotId)
}
