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
            require(size >= 0) { "size ($size) must be greater than or equal to 0" }
            require(size >= content.size) {
                "totalCount ($size) cannot be smaller than content.size (${content.size})"
            }
            return Cursor(content, nextCursor, size)
        }
    }
}
