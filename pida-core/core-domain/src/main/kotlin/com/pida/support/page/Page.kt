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
            require(totalCount >= 0) { "totalCount ($totalCount)는 음수일 수 없습니다" }
            require(totalCount >= content.size) {
                "totalCount ($totalCount)는 content.size (${content.size})보다 작을 수 없습니다"
            }
            val count = if (content.isEmpty()) 0 else totalCount
            return Page(content, count)
        }
    }
}
