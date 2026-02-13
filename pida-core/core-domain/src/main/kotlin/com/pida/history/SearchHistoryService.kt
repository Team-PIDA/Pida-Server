package com.pida.history

import com.pida.flowerspot.FlowerSpotSearchEvent
import com.pida.support.extension.logger
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SearchHistoryService(
    private val searchHistoryAppender: SearchHistoryAppender,
) {
    private val logger by logger()

    @Async
    @EventListener
    fun handleSearchEvent(event: FlowerSpotSearchEvent) {
        logger.info("Recording search history: query='${event.query}', authenticated=${event.userId != null}")

        recordSearchHistory(
            query = event.query,
            userId = event.userId,
        )
    }

    fun recordSearchHistory(
        query: String,
        userId: Long?,
    ) {
        val searchHistory =
            when (userId) {
                null -> NewSearchHistory.Anonymous(query = query) // 익명 사용자
                else -> NewSearchHistory.Authenticated(userId = userId, query = query) // 로그인한 사용자
            }

        add(searchHistory)
    }

    fun add(newSearchHistory: NewSearchHistory) {
        searchHistoryAppender.add(newSearchHistory)
    }
}
