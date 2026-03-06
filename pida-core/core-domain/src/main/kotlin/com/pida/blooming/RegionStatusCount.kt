package com.pida.blooming

import com.pida.support.geo.Region

/**
 * 지역별 BloomingStatus 투표 집계 결과
 *
 * @property region 지역
 * @property status 개화 상태
 * @property count 투표 수
 */
data class RegionStatusCount(
    val region: Region,
    val status: BloomingStatus,
    val count: Long,
)
