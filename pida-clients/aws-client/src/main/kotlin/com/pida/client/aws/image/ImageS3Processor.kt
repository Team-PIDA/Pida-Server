package com.pida.client.aws.image

import com.pida.client.aws.config.AwsProperties
import com.pida.client.aws.s3.AwsS3Client
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.PresignedUrlRateLimiter
import com.pida.support.aws.S3ImageUrl
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ImageS3Processor(
    private val awsS3Client: AwsS3Client,
    private val awsProperties: AwsProperties,
    private val imageFileConstructor: ImageFileConstructor,
    private val rateLimiter: PresignedUrlRateLimiter,
) : ImageS3Caller {
    override fun createUploadUrl(
        userId: Long,
        prefix: String,
        prefixId: Long,
    ): S3ImageUrl {
        rateLimiter.consume(userId)

        val imageFilePath = imageFileConstructor.imageFilePath(prefix, prefixId)
        val imageFileName = imageFileConstructor.imageFileName()

        val presignedUrl =
            awsS3Client.generateUploadUrl(
                awsProperties.s3.bucket,
                imageFilePath,
                imageFileName,
                Duration.ofSeconds(30), // 만료 시간 최소화
            )

        return S3ImageUrl(
            presignedUrl,
            presignedGet(imageFilePath, imageFileName),
        )
    }

    override suspend fun getImageUrl(
        prefix: String,
        prefixId: Long,
        fileName: String?,
    ): List<String> {
        val imageFilePath = imageFileConstructor.imageFilePath(prefix, prefixId)

        return fileName
            ?.let {
                listOf(presignedGet(imageFilePath, it)) // fileName이 있으면 특정 이미지 조회
            } ?: listPresignedGets(imageFilePath) // 아니면 해당 경로 아래 모든 이미지 탐색
    }

    private fun presignedGet(
        filePath: String,
        fileName: String,
        ttl: Duration = Duration.ofSeconds(30),
    ): String =
        awsS3Client.generateUrl(
            bucketName = awsProperties.s3.bucket,
            filePath = filePath,
            fileName = fileName,
            ttl = ttl,
        )

    private fun listPresignedGets(
        filePath: String,
        ttl: Duration = Duration.ofSeconds(30),
    ): List<String> =
        awsS3Client
            .getBucketListObjects(
                bucketName = awsProperties.s3.bucket,
                filePath = filePath,
            ).contents()
            .orEmpty()
            .asSequence()
            .filterNot { it.key().endsWith("/") }
            .map { it.key().substringAfterLast("/") }
            .map { presignedGet(filePath, it, ttl) }
            .toList()
}
