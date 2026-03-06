package com.pida.user.location

import org.springframework.stereotype.Component

@Component
class UserLocationReader(
    private val userLocationRepository: UserLocationRepository,
) {
    fun readUserLocationByUserId(userId: Long) = userLocationRepository.findByUserId(userId)

    fun readUserLocationsByUserIds(userIds: List<Long>) = userLocationRepository.findByUserIds(userIds)

    fun readUserLocationsWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ) = userLocationRepository.findWithinRadius(latitude, longitude, radiusMeters)
}
