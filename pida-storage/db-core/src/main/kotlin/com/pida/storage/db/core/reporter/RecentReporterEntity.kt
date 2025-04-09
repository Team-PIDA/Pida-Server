package com.pida.storage.db.core.reporter

import com.pida.reporter.RecentReporter
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "recent_reporter")
class RecentReporterEntity(
    val userId: Long,
    val flowerSpotId: Long,
) : BaseEntity() {
    fun toRecentReporter(): RecentReporter =
        RecentReporter(
            id = id!!,
            userId = userId,
            flowerSpotId = flowerSpotId,
            updatedAt = updatedAt,
        )
}
