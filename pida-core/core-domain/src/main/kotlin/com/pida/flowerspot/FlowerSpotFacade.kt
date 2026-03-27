package com.pida.flowerspot

import com.fasterxml.jackson.core.type.TypeReference
import com.pida.blooming.BloomingService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.S3ImageInfo
import com.pida.support.cache.Cache
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
    companion object {
        const val PREVIEW_IMAGE_KEY = "spot:preview"
        const val PREVIEW_IMAGE_TTL = 10L
    }

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
            val flowerSpots = flowerSpotService.readAllFlowerSpot(region, location)
            if (flowerSpots.isEmpty()) return@coroutineScope emptyList()

            val bloomingDeferred = async { bloomingService.recentlyBloomingBySpotIds(flowerSpots.map { it.id }) }

            val s3Start = System.currentTimeMillis()
            val previewDeferred =
                flowerSpots.map { spot ->
                    async {
                        spot.id to
                            imageS3Caller.getPreviewImage(
                                prefix = ImagePrefix.FLOWERSPOT.value,
                                prefixId = spot.id,
                            )
                    }
                }

            val previewBySpotId = previewDeferred.associate { it.await() }
            logger.info("s3 preview fetch time ${System.currentTimeMillis() - s3Start}ms (${flowerSpots.size} spots)")
            val bloomingBySpotId = bloomingDeferred.await().groupBy { it.flowerSpotId }

            flowerSpots.map { flowerSpot ->
                FlowerSpotDetails.of(
                    flowerSpot = flowerSpot,
                    bloomings = bloomingBySpotId[flowerSpot.id] ?: emptyList(),
                    images = listOfNotNull(previewBySpotId[flowerSpot.id]),
                )
            }
        }

    private suspend fun cachedPreviewImage(spotId: Long): S3ImageInfo? =
        Cache.cache(
            ttl = PREVIEW_IMAGE_TTL,
            key = "$PREVIEW_IMAGE_KEY:$spotId",
            typeReference = object : TypeReference<S3ImageInfo?>() {},
        ) {
            imageS3Caller.getPreviewImage(
                prefix = ImagePrefix.FLOWERSPOT.value,
                prefixId = spotId,
            )
        }
}
