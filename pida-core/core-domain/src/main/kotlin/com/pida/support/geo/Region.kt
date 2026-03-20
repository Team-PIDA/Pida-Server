package com.pida.support.geo

enum class Region {
    SEOUL,
    GYEONGGI,
    BUSAN,
    DAEGU,
    INCHEON,
    GWANGJU,
    DAEJEON,
    ULSAN,
    SEJONG,
    GANGWON,
    CHUNGBUK,
    CHUNGNAM,
    JEONBUK,
    JEONNAM,
    GYEONGBUK,
    GYEONGNAM,
    JEJU,
    ;

    /**
     * Region을 한글 이름으로 변환합니다.
     */
    fun toKoreanName(): String =
        when (this) {
            SEOUL -> "서울"
            GYEONGGI -> "경기"
            BUSAN -> "부산"
            DAEGU -> "대구"
            INCHEON -> "인천"
            GWANGJU -> "광주"
            DAEJEON -> "대전"
            ULSAN -> "울산"
            SEJONG -> "세종"
            GANGWON -> "강원"
            CHUNGBUK -> "충북"
            CHUNGNAM -> "충남"
            JEONBUK -> "전북"
            JEONNAM -> "전남"
            GYEONGBUK -> "경북"
            GYEONGNAM -> "경남"
            JEJU -> "제주"
        }
}

fun String.toRegion(): Region =
    when {
        startsWith("서울") || startsWith("서울특별시") -> Region.SEOUL
        startsWith("경기") || startsWith("경기도") -> Region.GYEONGGI
        startsWith("부산") -> Region.BUSAN
        startsWith("대구") -> Region.DAEGU
        startsWith("인천") -> Region.INCHEON
        startsWith("광주") -> Region.GWANGJU
        startsWith("대전") -> Region.DAEJEON
        startsWith("울산") -> Region.ULSAN
        startsWith("세종") -> Region.SEJONG
        startsWith("강원") -> Region.GANGWON
        startsWith("충북") || startsWith("충청북") -> Region.CHUNGBUK
        startsWith("충남") || startsWith("충청남") -> Region.CHUNGNAM
        startsWith("전북") || startsWith("전라북") -> Region.JEONBUK
        startsWith("전남") || startsWith("전라남") -> Region.JEONNAM
        startsWith("경북") || startsWith("경상북") -> Region.GYEONGBUK
        startsWith("경남") || startsWith("경상남") -> Region.GYEONGNAM
        startsWith("제주") -> Region.JEJU
        else -> Region.SEOUL
    }
