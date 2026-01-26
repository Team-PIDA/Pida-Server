package com.pida.storage.db.core.history

import org.springframework.data.jpa.repository.JpaRepository

interface SearchHistoryJpaRepository : JpaRepository<SearchHistoryEntity, Long>
