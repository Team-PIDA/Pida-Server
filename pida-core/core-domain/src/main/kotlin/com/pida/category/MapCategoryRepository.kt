package com.pida.category

interface MapCategoryRepository {
    suspend fun findBy(id: Long): MapCategory

    suspend fun findAll(): List<MapCategory>
}
