package com.pida.client.weather

/**
 * 기상청 단기예보 API 응답
 */
data class KmaWeatherResponse(
    val response: Response,
) {
    data class Response(
        val header: Header,
        val body: Body,
    )

    data class Header(
        val resultCode: String,
        val resultMsg: String,
    )

    data class Body(
        val dataType: String,
        val items: Items,
        val pageNo: Int,
        val numOfRows: Int,
        val totalCount: Int,
    )

    data class Items(
        val item: List<Item>,
    )

    data class Item(
        // 발표일자
        val baseDate: String,
        // 발표시각
        val baseTime: String,
        // 자료구분코드 (POP, PTY, PCP, REH, SNO, SKY, TMP, TMN, TMX, UUU, VVV, WAV, VEC, WSD)
        val category: String,
        // 예보일자
        val fcstDate: String,
        // 예보시각
        val fcstTime: String,
        // 예보값
        val fcstValue: String,
        // 예보지점 X 좌표
        val nx: Int,
        // 예보지점 Y 좌표
        val ny: Int,
    )
}
