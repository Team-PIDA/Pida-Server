package com.pida.place

interface DistrictRepository {
    fun saveAll(districts: List<District>)

    fun searchByKeyword(keyword: String): List<District>
}
