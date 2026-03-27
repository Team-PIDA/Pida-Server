package com.pida.client.aws.s3

import kotlinx.coroutines.future.await
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.S3Object

@Component
class AwsS3AsyncClient(
    private val s3AsyncClient: S3AsyncClient,
) {
    suspend fun listObjects(
        bucketName: String,
        filePath: String,
    ): List<S3Object> {
        val allObjects = mutableListOf<S3Object>()
        var continuationToken: String? = null

        do {
            val request =
                ListObjectsV2Request
                    .builder()
                    .bucket(bucketName)
                    .prefix(filePath)
                    .maxKeys(1000)
                    .continuationToken(continuationToken)
                    .build()

            val response = s3AsyncClient.listObjectsV2(request).await()
            allObjects.addAll(response.contents())
            continuationToken = response.nextContinuationToken()
        } while (response.isTruncated)

        return allObjects
    }
}
