package com.pida.history

data class SearchHistory(
    val id: Long,
    val userId: Long?,
    val query: String,
)

sealed interface NewSearchHistory {
    val query: String

    data class Authenticated(
        val userId: Long,
        override val query: String,
    ) : NewSearchHistory

    data class Anonymous(
        override val query: String,
    ) : NewSearchHistory
}
