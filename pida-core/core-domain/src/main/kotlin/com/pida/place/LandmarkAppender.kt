package com.pida.place

import com.pida.support.tx.TransactionTemplates
import org.springframework.stereotype.Component

@Component
class LandmarkAppender(
    private val landmarkRepository: LandmarkRepository,
    private val tx: TransactionTemplates,
) {
    fun addAll(newLandmarks: List<NewLandmark>) {
        tx.writer.execute {
            landmarkRepository.saveAll(newLandmarks)
            landmarkRepository.updateNameTsv()
        }
    }
}
