package com.pida.storage.db.core.history

import com.pida.history.SearchHistory
import com.pida.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "t_search_history")
class SearchHistoryEntity(
    val userId: Long?,
    val query: String,
) : BaseEntity() {
    fun toSearchHistory(): SearchHistory =
        SearchHistory(
            id = id!!,
            userId = userId,
            query = query,
        )
}
