package com.pida.presentation.v1.blooming.response

import com.pida.blooming.BloomingDetails

data class BloomingDetailsResponse(
    val totalCount: Long,
    val details: Map<String, Map<String, Int>>,
) {
    companion object {
        fun from(bloomingDetails: BloomingDetails): BloomingDetailsResponse =
            BloomingDetailsResponse(
                totalCount = bloomingDetails.totalCount,
                details = bloomingDetails.details,
            )
    }
}
