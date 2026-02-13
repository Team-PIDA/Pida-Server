package com.pida.user.location

interface UserLocationRepository {
    fun findByUserId(userId: Long): UserLocation.Info?

    fun findByUserIds(userIds: List<Long>): List<UserLocation.Info>

    fun saveOrUpdate(
        userId: Long,
        latitude: Double,
        longitude: Double,
    ): UserLocation.Info
}
