package com.pida.storage.db.core.history

import com.pida.history.NewSearchHistory
import com.pida.history.SearchHistoryRepository
import org.springframework.stereotype.Repository

@Repository
class SearchHistoryCoreRepository(
    private val searchHistoryJpaRepository: SearchHistoryJpaRepository,
) : SearchHistoryRepository {
    override fun save(newSearchHistory: NewSearchHistory) {
        val entity =
            when (newSearchHistory) {
                is NewSearchHistory.Authenticated ->
                    SearchHistoryEntity(
                        userId = newSearchHistory.userId,
                        query = newSearchHistory.query,
                    )

                is NewSearchHistory.Anonymous ->
                    SearchHistoryEntity(
                        userId = null,
                        query = newSearchHistory.query,
                    )
            }

        searchHistoryJpaRepository.save(entity)
    }
}
