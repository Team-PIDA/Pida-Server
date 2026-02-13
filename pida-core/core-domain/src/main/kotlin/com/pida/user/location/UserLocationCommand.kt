package com.pida.user.location

data class UserLocationCommand(
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
)
