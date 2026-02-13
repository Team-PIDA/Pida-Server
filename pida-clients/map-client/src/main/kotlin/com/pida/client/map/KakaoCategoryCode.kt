package com.pida.client.map

import com.pida.place.LandmarkCategory

private enum class KakaoCategoryCode(
    val landmarkCategory: LandmarkCategory,
) {
    SW8(LandmarkCategory.SUBWAY),
    AT4(LandmarkCategory.TOUR_SPOT),
    CE7(LandmarkCategory.CAFE),
    FD6(LandmarkCategory.RESTAURANT),
    ;

    companion object {
        private val CODE_MAP = entries.associateBy { it.name }

        fun from(code: String): KakaoCategoryCode? = CODE_MAP[code]
    }
}

fun KakaoPlaceDocument.toLandmarkCategory(): LandmarkCategory? = KakaoCategoryCode.from(categoryGroupCode)?.landmarkCategory
