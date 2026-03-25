package com.pida.notification.bloomed

/**
 * BLOOMED 알림 유형
 *
 * - FIRST_VOTE: 개화 초기 — 지역의 첫 BLOOMED 투표 시 발송
 * - THRESHOLD_REACHED: 만개 절정 — 지역 BLOOMED 비율 80% 이상 시 발송
 */
enum class BloomedAlertType {
    FIRST_VOTE,
    THRESHOLD_REACHED,
}
