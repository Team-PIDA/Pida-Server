package com.pida.client.map

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoRegionResponse(
    val meta: KakaoRegionMeta,
    val documents: List<KakaoRegionDocument>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoRegionMeta(
    @field:JsonProperty("total_count")
    val totalCount: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoRegionDocument(
    @field:JsonProperty("region_type")
    val regionType: String,
    @field:JsonProperty("address_name")
    val addressName: String,
    @field:JsonProperty("region_1depth_name")
    val region1depthName: String,
    @field:JsonProperty("region_2depth_name")
    val region2depthName: String,
    @field:JsonProperty("region_3depth_name")
    val region3depthName: String,
    @field:JsonProperty("region_4depth_name")
    val region4depthName: String,
    val code: String,
    val x: Double,
    val y: Double,
)
