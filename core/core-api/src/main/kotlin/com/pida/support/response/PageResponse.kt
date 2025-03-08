package com.pida.support.response

data class PageResponse<Data>(
    val content: Data? = null,
    val totalPages: Long = 0L,
    val totalElements: Long = 0L,
) {
    companion object {
        fun <Data> of(
            content: Data?,
            totalPages: Long,
            totalElements: Long,
        ): PageResponse<Data> = PageResponse(content, totalPages, totalElements)
    }
}
