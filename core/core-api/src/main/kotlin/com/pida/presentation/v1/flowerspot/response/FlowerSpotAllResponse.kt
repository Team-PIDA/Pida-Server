package com.pida.presentation.v1.flowerspot.response

import com.pida.flowerspot.FlowerSpot

data class FlowerSpotAllResponse(
    val list: List<FlowerSpot>,
) {
    companion object {
        fun of(list: List<FlowerSpot>) = FlowerSpotAllResponse(list)
    }
}
