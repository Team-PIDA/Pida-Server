package com.pida.storage.db.core.reporter

import com.pida.reporter.RecentReporter
import com.pida.reporter.RecentReporterRepository
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecuteNullable
import org.springframework.stereotype.Repository

@Repository
class RecentReporterCoreRepository(
    private val recentReporterJpaRepository: RecentReporterJpaRepository,
    private val tx: TransactionTemplates,
) : RecentReporterRepository {
    override suspend fun findRecentlyByFlowerSpotId(flowerSpotId: Long): RecentReporter? =
        tx.reader.coExecuteNullable {
            recentReporterJpaRepository
                .findTopByFlowerSpotIdOrderByUpdatedAtDesc(flowerSpotId)
                ?.toRecentReporter()
        }
}
