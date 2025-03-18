package com.pida.flowerspot

import java.time.LocalDateTime

data class FlowerSpot(
    val id: Long,
    val latitude: String,
    val longitude: String,
    val address: String,
    val streetName: String,
    val district: String?,
    val description: String?,
    val pinPoint: String,
    val region: Region,
    val deletedAt: LocalDateTime?,
)
