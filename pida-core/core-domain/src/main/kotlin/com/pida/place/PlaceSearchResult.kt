package com.pida.place

import com.pida.flowerspot.FlowerSpot

data class PlaceSearchResult(
    val districts: List<District>,
    val landmarks: List<Landmark>,
    val flowerSpots: List<FlowerSpot>,
)
