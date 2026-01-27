package com.pida.landmark

interface LandmarkSearchClient {
    fun searchByKeyword(query: String): List<NewLandmark>
}
