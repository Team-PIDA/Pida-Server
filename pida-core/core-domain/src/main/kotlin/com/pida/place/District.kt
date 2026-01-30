package com.pida.place

import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region

data class District(
    val id: Long,
    val sido: Region,
    val sigungu: String,
    val eupmyeondong: String?,
    val ri: String?,
    val pinPoint: GeoJson,
)
