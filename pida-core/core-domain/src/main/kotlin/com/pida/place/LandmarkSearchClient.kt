package com.pida.place

interface LandmarkSearchClient {
    fun searchByKeyword(query: String): List<NewLandmark>
}
