package com.pida.flowerspot

import com.pida.support.aws.ImageS3Caller
import org.springframework.stereotype.Service

@Service
class FlowerSpotCafeFacade(
    private val flowerSpotCafeFinder: FlowerSpotCafeFinder,
    private val flowerSpotCafeAppender: FlowerSpotCafeAppender,
    private val imageS3Caller: ImageS3Caller,
) {
    suspend fun processBatch(requests: List<NewFlowerSpotCafe>) = requests.forEach { flowerSpotCafeAppender.add(it.toDomain()) }

    suspend fun uploadThumbnail(
        cafeId: Long,
        imageBytes: ByteArray,
    ) {
        flowerSpotCafeFinder.readBy(cafeId)

        val uploadResult =
            imageS3Caller.uploadImage(
                prefix = "flowerspotcafe",
                prefixId = cafeId,
                subPath = "thumbnail",
                contentType = "image/jpeg",
                bytes = imageBytes,
            )

        flowerSpotCafeAppender.updateThumbnailUrl(cafeId, uploadResult.publicUrl)
    }
}
