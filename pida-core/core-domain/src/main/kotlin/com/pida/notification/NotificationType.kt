package com.pida.notification

enum class NotificationType {
    ADMIN_PUSH, // 관리자 푸시
    REGULAR, // 정기 푸시 알림 (화요일 오전 9시)
    WEEKEND_HEALING, // 주말 힐링 푸시 알림
    WEEKDAY_HEALING, // 평일 힐링 푸시 알림
    ;

    companion object {
        fun of(type: String): NotificationType =
            when (type) {
                "ADMIN_PUSH" -> ADMIN_PUSH
                "REGULAR" -> REGULAR
                "WEEKEND_HEALING" -> WEEKEND_HEALING
                "WEEKDAY_HEALING" -> WEEKDAY_HEALING
                else -> throw IllegalArgumentException("Unknown NotificationType: $type")
            }
    }
}
