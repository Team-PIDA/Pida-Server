package com.pida.reporter

interface RecentReporterRepository {
    suspend fun findRecentlyByFlowerSpotId(flowerSpotId: Long): RecentReporter?
}
