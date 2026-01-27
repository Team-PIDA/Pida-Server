package com.pida.history

interface SearchHistoryRepository {
    fun save(newSearchHistory: NewSearchHistory)
}
