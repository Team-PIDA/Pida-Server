package com.pida.flowerspot

import com.pida.blooming.BloomingService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.S3ImageInfo
import com.pida.support.geo.Region
import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FlowerSpotFacade(
    private val flowerSpotService: FlowerSpotService,
    private val bloomingService: BloomingService,
    private val imageS3Caller: ImageS3Caller,
    private val externalDependencyPolicy: ExternalDependencyPolicy,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun readFlowerSpotDetails(spotId: Long): FlowerSpotDetails =
        coroutineScope {
            val flowerSpotDeferred = async { flowerSpotService.readOneFlowerSpot(spotId) }
            val bloomings = async { bloomingService.recentlyBloomingBySpotId(spotId) }
            val images =
                async {
                    runCatching {
                        imageS3Caller.getImageUrl(
                            prefix = ImagePrefix.FLOWERSPOT.value,
                            prefixId = spotId,
                            fileName = null,
                        )
                    }.getOrElse { error ->
                        externalDependencyPolicy.recordFallback(
                            dependency = ExternalDependency.AWS_S3,
                            reason = "empty-image-list",
                            throwable = error,
                        )
                        logger.warn("Falling back to empty flower spot image list for spotId={}", spotId, error)
                        emptyList()
                    }
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

            val bloomingBySpotId =
                bloomingService
                    .recentlyBloomingBySpotIds(flowerSpots.map { it.id })
                    .groupBy { it.flowerSpotId }

            flowerSpots.map { flowerSpot ->
                FlowerSpotDetails.of(
                    flowerSpot = flowerSpot,
                    bloomings = bloomingBySpotId[flowerSpot.id] ?: emptyList(),
                    images = listOfNotNull(previewImagePresignedUrl(flowerSpot)),
                )
            }
        }

    private fun previewImagePresignedUrl(flowerSpot: FlowerSpot): S3ImageInfo? =
        flowerSpot.previewImageKey?.let { key ->
            runCatching {
                S3ImageInfo(
                    url = imageS3Caller.generatePresignedUrl(key),
                    uploadedAt = flowerSpot.previewImageUploadedAt!!,
                )
            }.getOrElse { error ->
                externalDependencyPolicy.recordFallback(
                    dependency = ExternalDependency.AWS_S3,
                    reason = "empty-preview-image",
                    throwable = error,
                )
                logger.warn("Falling back to empty flower spot preview image for spotId={}", flowerSpot.id, error)
                null
            }
        }
}
