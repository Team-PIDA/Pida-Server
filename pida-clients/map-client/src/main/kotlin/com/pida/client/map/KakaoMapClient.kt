package com.pida.client.map

import com.pida.place.LandmarkSearchClient
import com.pida.place.NewLandmark
import com.pida.support.extension.logger
import com.pida.support.geo.toRegion
import org.springframework.stereotype.Component

@Component
class KakaoMapClient internal constructor(
    private val kakaoMapApi: KakaoMapApi,
    private val kakaoMapProperties: KakaoMapProperties,
) : LandmarkSearchClient {
    private val logger by logger()

    override fun searchByKeyword(query: String): List<NewLandmark> {
        val response =
            kakaoMapApi.searchKeyword(
                authorization = "KakaoAK ${kakaoMapProperties.restApiKey}",
                query = query,
                categoryGroupCode = null,
                x = null,
                y = null,
                radius = null,
            )

        logger.debug("Kakao Map search API requested: query='$query', found=${response.documents.size}")

        return response.documents.map { document ->
            NewLandmark(
                name = document.placeName,
                address = document.addressName,
                x = document.x.toDouble(),
                y = document.y.toDouble(),
                region = document.addressName.toRegion(),
                category = document.toLandmarkCategory(),
            )
        }
    }
}
