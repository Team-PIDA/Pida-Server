package com.pida.storage.db.core.user.device

import com.pida.storage.db.core.support.BaseEntity
import com.pida.user.device.UserDevice
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "t_user_device")
class UserDeviceEntity(
    private val userId: Long,
    private val userKey: String,
    private val fcmToken: String,
    private val deviceId: String,
) : BaseEntity() {
    constructor(create: UserDevice.Create) : this(
        userId = create.userId,
        userKey = create.userKey,
        fcmToken = create.fcmToken,
        deviceId = create.deviceId,
    )

    fun toUserDevice() =
        UserDevice.Info(
            id = id!!,
            userId = userId,
            userKey = userKey,
            fcmToken = fcmToken,
            deviceId = deviceId,
            createdAt = createdAt,
        )
}
