package com.pida.flowerspot

import com.pida.place.Landmark

data class FlowerSpotSearchResult(
    val landmarks: List<Landmark>,
    val flowerSpots: List<FlowerSpot>,
)
