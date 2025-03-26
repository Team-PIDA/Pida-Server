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
    suspend fun readFlowerSpotDetails(spotId: Long): FlowerSpotDetails =
        coroutineScope {
            val flowerSpotDeferred = async { flowerSpotService.readOneFlowerSpot(spotId) }
            val bloomings = async { bloomingService.recentlyBloomingBySpotId(spotId) }

            return@coroutineScope FlowerSpotDetails.of(
                flowerSpot = flowerSpotDeferred.await(),
                bloomings = bloomings.await().groupBy { it.flowerSpotId }[spotId] ?: emptyList(),
            )
        }

    suspend fun findAllFlowerSpot(
        region: Region?,
        location: FlowerSpotLocation,
    ): List<FlowerSpotDetails> {
        val flowerSpots = flowerSpotService.readAllFlowerSpot(region, location)
        val recentlyBlooming = bloomingService.recentlyBloomingBySpotIds(flowerSpots.map { it.id })

        return flowerSpots.map { flowerSpot ->
            FlowerSpotDetails.of(
                flowerSpot = flowerSpot,
                bloomings = recentlyBlooming.groupBy { it.flowerSpotId }[flowerSpot.id] ?: emptyList(),
            )
        }
    }
}
