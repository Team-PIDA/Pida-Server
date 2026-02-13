package com.pida.support.cursor

data class Cursor<T>(
    val content: List<T>,
    val nextCursor: Long?,
    val size: Long,
) {
    companion object {
        const val DEFAULT_CURSOR = 0L
        const val DEFAULT_SIZE = 20

        fun <T> of(
            content: List<T>,
            nextCursor: Long?,
            size: Long,
        ): Cursor<T> {
            require(size >= 0) { "size ($size)는 0 이상이어야 합니다" }
            require(size >= content.size) {
                "size ($size)는 content.size (${content.size})보다 작을 수 없습니다"
            }
            return Cursor(content, nextCursor, size)
        }
    }
}
