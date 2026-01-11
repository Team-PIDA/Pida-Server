package com.pida.user.location

import org.springframework.stereotype.Service

@Service
class UserLocationService(
    private val userLocationRepository: UserLocationRepository,
) {
    fun updateUserLocation(
        userId: Long,
        latitude: Double,
        longitude: Double,
    ): UserLocation.Info = userLocationRepository.saveOrUpdate(userId, latitude, longitude)
}
