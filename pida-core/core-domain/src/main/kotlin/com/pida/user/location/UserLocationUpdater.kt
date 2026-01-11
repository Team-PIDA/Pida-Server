package com.pida.user.location

import org.springframework.stereotype.Component

@Component
class UserLocationUpdater(
    private val userLocationRepository: UserLocationRepository,
) {
    fun updateUserLocation(
        userId: Long,
        latitude: Double,
        longitude: Double,
    ): UserLocation.Info = userLocationRepository.saveOrUpdate(userId, latitude, longitude)
}
