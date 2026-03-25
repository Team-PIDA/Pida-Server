package com.pida.flowerevent

import com.pida.support.aws.ImageS3Caller
import org.springframework.stereotype.Service

@Service
class FlowerEventFacade(
    private val flowerEventFinder: FlowerEventFinder,
    private val flowerEventAppender: FlowerEventAppender,
    private val imageS3Caller: ImageS3Caller,
) {
    suspend fun addAll(requests: List<NewFlowerEvent>) = requests.forEach { flowerEventAppender.add(it.toDomain()) }

    suspend fun uploadThumbnail(
        eventId: Long,
        imageBytes: ByteArray,
    ) {
        flowerEventFinder.readBy(eventId)

        val uploadResult =
            imageS3Caller.uploadImage(
                prefix = "flowerevent",
                prefixId = eventId,
                subPath = "thumbnail",
                contentType = "image/jpeg",
                bytes = imageBytes,
            )

        flowerEventAppender.updateThumbnailUrl(eventId, uploadResult.publicUrl)
    }
}
