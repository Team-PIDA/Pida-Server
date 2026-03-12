package com.pida.storage.db.core.category

import com.pida.category.MapCategory
import com.pida.category.MapCategoryRepository
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.coExecute
import org.springframework.stereotype.Repository

@Repository
class MapCategoryCoreRepository(
    private val mapCategoryJpaRepository: MapCategoryJpaRepository,
    private val tx: TransactionTemplates,
) : MapCategoryRepository {
    override suspend fun findAll(): List<MapCategory> =
        tx.reader.coExecute {
            mapCategoryJpaRepository
                .findByDeletedAtIsNull()
                .map { it.toMapCategory() }
        }
}
