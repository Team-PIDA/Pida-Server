package com.pida.support.cursor

data class CursorRequest(
    val lastId: Long?,
    val size: Long,
) {
    init {
        require(size in 1..100) { "크기는 1 ~ 100 사이여야 합니다." }
    }
}
