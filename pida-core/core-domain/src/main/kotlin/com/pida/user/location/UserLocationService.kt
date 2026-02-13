package com.pida.user.location

import org.springframework.stereotype.Service

@Service
class UserLocationService(
    private val userLocationRepository: UserLocationRepository,
) {
    fun updateUserLocation(command: UserLocationCommand): UserLocation.Info =
        userLocationRepository.saveOrUpdate(command.userId, command.latitude, command.longitude)
}
