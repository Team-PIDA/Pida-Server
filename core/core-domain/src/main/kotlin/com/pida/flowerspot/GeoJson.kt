package com.pida.flowerspot

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = GeoJson.Point::class, name = "Point"),
    JsonSubTypes.Type(value = GeoJson.LineString::class, name = "LineString"),
    JsonSubTypes.Type(value = GeoJson.Polygon::class, name = "Polygon"),
)
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
