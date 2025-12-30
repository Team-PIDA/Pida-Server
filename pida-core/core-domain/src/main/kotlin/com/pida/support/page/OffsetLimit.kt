package com.pida.support.page

data class OffsetLimit(
    val offset: Long,
    val limit: Long,
    val type: SortType? = SortType.NEW,
    val sort: Sort? = Sort.DESC,
) {
    init {
        require(offset >= 0) { "offset은 0보다 커야 합니다. " }
        require(limit > 0) { "limit은 0보다 커야 합니다. " }
    }

    companion object {
        fun of(
            offset: Long,
            limit: Long,
            type: SortType? = SortType.NEW,
            sort: Sort? = Sort.DESC,
        ): OffsetLimit = OffsetLimit(offset, limit, type, sort)
    }
}
