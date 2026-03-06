package com.pida.notification

import com.pida.support.geo.Region

/**
 * 알림 발송 대상 사용자 정보 (지역 포함)
 *
 * @property userId 사용자 ID
 * @property latitude 위도
 * @property longitude 경도
 * @property region 사용자가 속한 지역
 */
data class EligibleUserWithRegion(
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
    val region: Region,
)
