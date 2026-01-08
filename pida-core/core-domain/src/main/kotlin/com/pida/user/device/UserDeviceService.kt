package com.pida.user.device

import org.springframework.stereotype.Service

@Service
class UserDeviceService(
    private val userDeviceAppender: UserDeviceAppender,
    private val userDeviceReader: UserDeviceReader,
) {
    fun append(create: UserDevice.Create) {
        userDeviceAppender.append(create)
    }

    fun findAll(): List<UserDevice.Info> = userDeviceReader.readAll()

    fun findByUserId(userId: Long): UserDevice.Info? = userDeviceReader.readLastByUserId(userId)

    fun findAllByUserId(userId: Long): List<UserDevice.Info> = userDeviceReader.readAllByUserId(userId)

    fun findAllByUserKey(userKey: String): List<UserDevice.Info> = userDeviceReader.readAllByUserKey(userKey)
}
