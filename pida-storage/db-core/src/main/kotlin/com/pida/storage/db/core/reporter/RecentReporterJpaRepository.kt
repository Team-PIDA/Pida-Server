package com.pida.storage.db.core.reporter

import org.springframework.data.jpa.repository.JpaRepository

interface RecentReporterJpaRepository : JpaRepository<RecentReporterEntity, Long> {
    fun findTopByFlowerSpotIdOrderByUpdatedAtDesc(flowerSpotId: Long): RecentReporterEntity?
}
