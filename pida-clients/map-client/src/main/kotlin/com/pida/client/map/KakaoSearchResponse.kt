package com.pida.client.map

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoSearchResponse(
    val meta: KakaoSearchMeta,
    val documents: List<KakaoPlaceDocument>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoSearchMeta(
    @field:JsonProperty("same_name")
    val sameName: SameName,
    @field:JsonProperty("pageable_count")
    val pageableCount: Int,
    @field:JsonProperty("total_count")
    val totalCount: Int,
    @field:JsonProperty("is_end")
    val isEnd: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SameName(
    val region: List<String>,
    val keyword: String,
    @field:JsonProperty("selected_region")
    val selectedRegion: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoPlaceDocument(
    val id: String,
    @field:JsonProperty("place_name")
    val placeName: String,
    @field:JsonProperty("place_url")
    val placeUrl: String,
    @field:JsonProperty("category_name")
    val categoryName: String,
    @field:JsonProperty("category_group_code")
    val categoryGroupCode: String,
    @field:JsonProperty("category_group_name")
    val categoryGroupName: String,
    @field:JsonProperty("address_name")
    val addressName: String,
    @field:JsonProperty("road_address_name")
    val roadAddressName: String,
    val phone: String,
    val x: String,
    val y: String,
    val distance: String,
)
