package com.pida.notification.bloomed

import com.pida.support.geo.Region

/**
 * BLOOMED 상태가 임계값을 초과한 지역 정보
 *
 * @property region 지역
 * @property bloomedPercentage BLOOMED 투표 비율 (%)
 * @property totalVotes 총 투표 수
 * @property bloomedVotes BLOOMED 투표 수
 */
data class BloomedRegion(
    val region: Region,
    val bloomedPercentage: Double,
    val totalVotes: Int,
    val bloomedVotes: Int,
)
