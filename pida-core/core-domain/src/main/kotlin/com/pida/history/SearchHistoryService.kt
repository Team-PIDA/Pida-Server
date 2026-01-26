package com.pida.history

import org.springframework.stereotype.Service

@Service
class SearchHistoryService(
    private val searchHistoryAppender: SearchHistoryAppender,
) {
    fun add(newSearchHistory: NewSearchHistory) {
        searchHistoryAppender.add(newSearchHistory)
    }
}
