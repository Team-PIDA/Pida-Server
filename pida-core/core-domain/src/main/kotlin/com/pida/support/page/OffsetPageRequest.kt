package com.pida.support.page

data class OffsetPageRequest(
    val page: Int,
    val size: Int,
) {
    init {
        require(page >= 0) { "페이지는 0 이상이야 합니다." }
        require(size in 1..100) { "크기는 1 ~ 100 사이여야 합니다." }
    }
}
