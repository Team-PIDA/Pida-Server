package com.pida.notification

import com.pida.blooming.BloomingStatus
import com.pida.support.geo.Region

/**
 * Region과 BloomingStatus별 투표 수
 */
data class RegionStatusCount(
    val region: Region,
    val status: BloomingStatus,
    val count: Long,
)
