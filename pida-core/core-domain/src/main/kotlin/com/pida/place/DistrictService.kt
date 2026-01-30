package com.pida.place

import org.springframework.stereotype.Service

@Service
class DistrictService(
    private val districtAppender: DistrictAppender,
) {
    fun addAll(districts: List<District>) {
        districtAppender.addAll(districts)
    }
}
