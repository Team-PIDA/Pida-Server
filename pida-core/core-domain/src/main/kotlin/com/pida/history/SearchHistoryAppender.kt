package com.pida.history

import org.springframework.stereotype.Component

@Component
class SearchHistoryAppender(
    private val searchHistoryRepository: SearchHistoryRepository,
) {
    fun add(newSearchHistory: NewSearchHistory) {
        searchHistoryRepository.save(newSearchHistory)
    }
}
