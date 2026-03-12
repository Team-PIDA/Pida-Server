package com.pida.category

interface MapCategoryRepository {
    suspend fun findAll(): List<MapCategory>
}
