package com.pida.flowerspot

sealed class GeoJson {
    abstract val type: String

    data class Point(
        val coordinates: List<Double>,
    ) : GeoJson() {
        override val type: String = "Point"
    }

    data class LineString(
        val coordinates: List<List<Double>>,
    ) : GeoJson() {
        override val type: String = "LineString"
    }

    data class Polygon(
        val coordinates: List<List<List<Double>>>,
    ) : GeoJson() {
        override val type: String = "Polygon"
    }
}
