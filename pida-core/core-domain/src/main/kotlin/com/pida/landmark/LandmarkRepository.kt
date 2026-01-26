package com.pida.landmark

interface LandmarkRepository {
    fun findAll(): List<Landmark>

    fun findByNameContaining(query: String): List<Landmark>

    fun saveAll(newLandmarks: List<NewLandmark>)

    fun existsByName(name: String): Boolean
}
