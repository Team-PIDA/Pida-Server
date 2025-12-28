package com.pida.support.page

data class Page<T>(
    val content: List<T>,
    val totalCount: Long,
) {
    companion object {
        fun <T> of(
            content: List<T>,
            totalCount: Long,
        ): Page<T> {
            require(totalCount >= 0) { "totalCount ($totalCount) cannot be negative" }
            require(totalCount >= content.size) {
                "totalCount ($totalCount) cannot be smaller than content.size (${content.size})"
            }
            val count = if (content.isEmpty()) 0 else totalCount
            return Page(content, count)
        }
    }
}
