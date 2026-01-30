package com.pida.place

import com.pida.support.tx.TransactionTemplates
import org.springframework.stereotype.Component

@Component
class DistrictAppender(
    private val districtRepository: DistrictRepository,
    private val tx: TransactionTemplates,
) {
    fun addAll(districts: List<District>) {
        tx.writer.execute {
            districtRepository.saveAll(districts)
        }
    }
}
