package com.pida.flowerspot

data class FlowerSpotLocation(
    val swLat: Double?,
    val swLng: Double?,
    val neLat: Double?,
    val neLng: Double?,
)

fun FlowerSpotLocation.isNotSet(): Boolean = swLat != null && swLng != null && neLat != null && neLng != null
