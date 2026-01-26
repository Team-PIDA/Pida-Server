package com.pida.history

import com.pida.flowerspot.FlowerSpotSearchEvent
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class SearchHistoryService(
    private val searchHistoryAppender: SearchHistoryAppender,
) {
    private val logger by logger()

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleSearchEvent(event: FlowerSpotSearchEvent) {
        logger.info("Recording search history: query='${event.query}', userId='${event.userId}'")

        return recordSearchHistory(
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
                null -> NewSearchHistory.Anonymous(query = query)
                else -> NewSearchHistory.Authenticated(userId = userId, query = query)
            }

        add(searchHistory)
    }

    fun add(newSearchHistory: NewSearchHistory) {
        searchHistoryAppender.add(newSearchHistory)
    }
}
