package com.pida.flowerspot

import com.pida.blooming.BloomingService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.S3ImageInfo
import com.pida.support.extension.logger
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
    private val logger by logger()

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
    ): List<FlowerSpotDetails> =
        coroutineScope {
            val t0 = System.currentTimeMillis()
            val flowerSpots = flowerSpotService.readAllFlowerSpot(region, location)
            logger.info("flowerSpot query: ${System.currentTimeMillis() - t0}ms (${flowerSpots.size} spots)")
            if (flowerSpots.isEmpty()) return@coroutineScope emptyList()

            val t1 = System.currentTimeMillis()
            val bloomingBySpotId =
                bloomingService
                    .recentlyBloomingBySpotIds(flowerSpots.map { it.id })
                    .groupBy { it.flowerSpotId }
            logger.info("blooming query: ${System.currentTimeMillis() - t1}ms")

            val t2 = System.currentTimeMillis()
            val result =
                flowerSpots.map { flowerSpot ->
                    val previewImage =
                        flowerSpot.previewImageKey?.let { key ->
                            S3ImageInfo(
                                url = imageS3Caller.generatePresignedUrl(key),
                                uploadedAt = flowerSpot.previewImageUploadedAt!!,
                            )
                        }
                    FlowerSpotDetails.of(
                        flowerSpot = flowerSpot,
                        bloomings = bloomingBySpotId[flowerSpot.id] ?: emptyList(),
                        images = listOfNotNull(previewImage),
                    )
                }
            logger.info("presigned url generation: ${System.currentTimeMillis() - t2}ms")
            logger.info("total: ${System.currentTimeMillis() - t0}ms")

            result
        }
}
