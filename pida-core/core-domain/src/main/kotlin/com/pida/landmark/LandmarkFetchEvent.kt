package com.pida.landmark

sealed interface LandmarkFetchEvent {
    val query: String

    data class Requested(
        override val query: String,
    ) : LandmarkFetchEvent

    data class Fetched(
        override val query: String,
        val landmarks: List<NewLandmark>,
    ) : LandmarkFetchEvent

    companion object {
        fun from(
            query: String,
            fetchedLandmark: List<NewLandmark>?,
        ): LandmarkFetchEvent =
            when (fetchedLandmark) {
                null -> Requested(query)
                else -> Fetched(query, fetchedLandmark)
            }
    }
}
