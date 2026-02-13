package com.pida.user.device

import org.springframework.stereotype.Component

@Component
class UserDeviceAppender(
    private val userDeviceRepository: UserDeviceRepository,
) {
    fun append(create: UserDevice.Create) {
        val pushMessageTargets = userDeviceRepository.findAllByUserKey(create.userKey)
        val exists = pushMessageTargets.any { it.fcmToken == create.fcmToken }
        if (exists) return
        if (pushMessageTargets.size >= 5) {
            val firstId = pushMessageTargets.minByOrNull { it.id }?.id ?: return
            userDeviceRepository.softDeleteBy(firstId)
        }
        userDeviceRepository.save(create)
    }
}
