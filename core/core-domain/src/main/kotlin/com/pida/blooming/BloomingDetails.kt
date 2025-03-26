package com.pida.blooming

data class BloomingDetails(
    val totalCount: Long,
    val details: Map<String, Map<String, Int>>,
)
