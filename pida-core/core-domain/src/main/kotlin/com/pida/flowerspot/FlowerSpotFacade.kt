package com.pida.flowerspot

import com.pida.blooming.BloomingService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.geo.Region
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class FlowerSpotFacade(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
    private val imageS3Caller: ImageS3Caller,
) {
    suspend fun readFlowerSpotDetails(spotId: Long): FlowerSpotDetails =
        coroutineScope {
            val flowerSpotDeferred = async { flowerSpotService.readOneFlowerSpot(spotId) }
            val bloomings = async { bloomingService.recentlyBloomingBySpotId(spotId) }
            val images =
                async {
                    imageS3Caller.getImageUrl(
                        prefix = ImagePrefix.FLOWERSPOT.value,
                        prefixId = spotId,
                        fileName = null,
                    )
                }

            return@coroutineScope FlowerSpotDetails.of(
                flowerSpot = flowerSpotDeferred.await(),
                bloomings = bloomings.await().groupBy { it.flowerSpotId }[spotId] ?: emptyList(),
                images = images.await(),
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
                images =
                    imageS3Caller.getImageUrl(
                        prefix = ImagePrefix.FLOWERSPOT.value,
                        prefixId = flowerSpot.id,
                        fileName = null,
                    ),
            )
        }
    }
}
