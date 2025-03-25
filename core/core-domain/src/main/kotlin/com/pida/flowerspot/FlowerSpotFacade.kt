package com.pida.flowerspot

import com.pida.blooming.BloomingService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class FlowerSpotFacade(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
) {
    suspend fun findOneFlowerSpot(spotId: Long): FlowerSpotDetails =
        coroutineScope {
            val flowerSpotDeferred = async { flowerSpotService.findOneFlowerSpot(spotId) }
            val recentlyVisitedCountDeferred = async { bloomingService.recentlyVisitedCountBySpotId(spotId) }

            return@coroutineScope FlowerSpotDetails.of(
                flowerSpot = flowerSpotDeferred.await(),
                recentlyVisitedCount = recentlyVisitedCountDeferred.await(),
            )
        }
}
