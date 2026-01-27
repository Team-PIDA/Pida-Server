package com.pida.flowerspot

data class FlowerSpotSearchEvent(
    val query: String,
    val userId: Long?,
)
