package com.pida.user.device

import java.time.LocalDateTime

class UserDevice {
    /**
     * UserDevice Create
     *
     * @property userId 사용자 아이디
     * @property fcmToken FCM 토큰
     * @property deviceId X-DEVICE-ID 아이디
     */
    data class Create(
        val userId: Long,
        val userKey: String,
        val fcmToken: String,
        val deviceId: String,
    )

    /**
     * UserDevice Info
     *
     * @property userId 사용자 아이디
     * @property fcmToken FCM 토큰
     * @property deviceId X-DEVICE-ID 아이디
     */
    data class Info(
        val id: Long,
        val userId: Long,
        val userKey: String,
        val fcmToken: String,
        val deviceId: String,
        val createdAt: LocalDateTime,
    )
}
