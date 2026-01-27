package com.pida.notification.weekend

/**
 * 푸시 알림 발송 대상 사용자
 */
data class EligibleUser(
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
)
