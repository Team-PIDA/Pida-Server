package com.pida.flowerspot

import com.pida.landmark.Landmark

data class FlowerSpotSearchResult(
    val landmarks: List<Landmark>,
    val flowerSpots: List<FlowerSpot>,
)
