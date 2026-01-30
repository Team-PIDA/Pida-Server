package com.pida.place

interface LandmarkRepository {
    fun findAll(): List<Landmark>

    fun findByNameContaining(query: String): List<Landmark>

    fun saveAll(newLandmarks: List<NewLandmark>)

    fun existsByName(name: String): Boolean

    fun updateNameTsv()
}
