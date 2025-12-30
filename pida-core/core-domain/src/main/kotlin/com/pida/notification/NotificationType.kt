package com.pida.notification

enum class NotificationType {
    ADMIN_PUSH, // 관리자 푸시
    REGULAR, // 정기 푸시 알림 (화요일 오전 9시)
    ;

    companion object {
        fun of(type: String): NotificationType =
            when (type) {
                "ADMIN_PUSH" -> ADMIN_PUSH
                "REGULAR" -> REGULAR
                else -> throw IllegalArgumentException("Unknown NotificationType: $type")
            }
    }
}
