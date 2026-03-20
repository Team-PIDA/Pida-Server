package com.pida.notification.withered

import com.pida.support.geo.Region

/**
 * WITHERED 상태가 임계값을 초과한 지역 정보
 *
 * @property region 지역
 * @property witheredPercentage WITHERED 투표 비율 (%)
 * @property totalVotes 총 투표 수
 * @property witheredVotes WITHERED 투표 수
 */
data class WitheredRegion(
    val region: Region,
    val witheredPercentage: Double,
    val totalVotes: Int,
    val witheredVotes: Int,
)
