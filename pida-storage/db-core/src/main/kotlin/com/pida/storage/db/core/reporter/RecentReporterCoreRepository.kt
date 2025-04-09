package com.pida.storage.db.core.reporter

import com.pida.reporter.RecentReporterRepository
import org.springframework.stereotype.Repository

@Repository
class RecentReporterCoreRepository(
    private val recentReporterJpaRepository: RecentReporterJpaRepository,
) : RecentReporterRepository
