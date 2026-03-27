package com.pida.client.aws.image

import com.pida.client.aws.config.AwsProperties
import com.pida.client.aws.s3.AwsS3AsyncClient
import com.pida.client.aws.s3.AwsS3Client
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.PresignedUrlRateLimiter
import com.pida.support.aws.S3ImageInfo
import com.pida.support.aws.S3ImageUrl
import com.pida.support.aws.S3UploadResult
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.model.S3Object
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class ImageS3Processor(
    private val awsS3Client: AwsS3Client,
    private val awsS3AsyncClient: AwsS3AsyncClient,
    private val awsProperties: AwsProperties,
    private val imageFileConstructor: ImageFileConstructor,
    private val rateLimiter: PresignedUrlRateLimiter,
) : ImageS3Caller {
    companion object {
        private val SEOUL_ZONE_ID: ZoneId = ZoneId.of("Asia/Seoul")
    }

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

        val s3Key = "$imageFilePath/$imageFileName"
        return S3ImageUrl(
            presignedUrl,
            generateGetUrl(imageFilePath, imageFileName),
            s3Key,
        )
    }

    override suspend fun getImageUrl(
        prefix: String,
        prefixId: Long,
        fileName: String?,
    ): List<S3ImageInfo> {
        val imageFilePath = imageFileConstructor.imageFilePath(prefix, prefixId)

        return fileName
            ?.let {
                listOf(presignedGet(imageFilePath, it)) // fileName이 있으면 특정 이미지 조회
            } ?: listPresignedGets(imageFilePath) // 아니면 해당 경로 아래 모든 이미지 탐색
    }

    override fun uploadImage(
        prefix: String,
        prefixId: Long,
        subPath: String,
        contentType: String,
        bytes: ByteArray,
    ): S3UploadResult {
        val filePath = imageFileConstructor.imageFilePath(prefix, prefixId)
        val fileName = imageFileConstructor.imageFileName()
        val s3Key = "$filePath/$subPath/$fileName"

        awsS3Client.putObject(
            bucketName = awsProperties.s3.bucket,
            key = s3Key,
            contentType = contentType,
            bytes = bytes,
        )

        return S3UploadResult(
            s3Key = s3Key,
            publicUrl = "${awsProperties.s3.imageOriginUrl}/$s3Key",
        )
    }

    override suspend fun getPreviewImage(
        prefix: String,
        prefixId: Long,
    ): S3ImageInfo? {
        val imageFilePath = imageFileConstructor.imageFilePath(prefix, prefixId)

        return awsS3AsyncClient
            .listObjects(
                bucketName = awsProperties.s3.bucket,
                filePath = imageFilePath,
            ).filterNot { it.key().endsWith("/") }
            .maxByOrNull(S3Object::lastModified)
            ?.toImageInfo(imageFilePath, Duration.ofSeconds(30))
    }

    override fun generatePresignedUrl(s3Key: String): String {
        val filePath = s3Key.substringBeforeLast("/")
        val fileName = s3Key.substringAfterLast("/")
        return generateGetUrl(filePath, fileName)
    }

    private fun generateGetUrl(
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

    private fun presignedGet(
        filePath: String,
        fileName: String,
        ttl: Duration = Duration.ofSeconds(30),
    ): S3ImageInfo {
        val url =
            awsS3Client.generateUrl(
                bucketName = awsProperties.s3.bucket,
                filePath = filePath,
                fileName = fileName,
                ttl = ttl,
            )
        val lastModified =
            awsS3Client.getObjectLastModified(
                bucketName = awsProperties.s3.bucket,
                key = "$filePath/$fileName",
            )
        return S3ImageInfo(
            url = url,
            uploadedAt = LocalDateTime.ofInstant(lastModified, SEOUL_ZONE_ID),
        )
    }

    private fun listPresignedGets(
        filePath: String,
        ttl: Duration = Duration.ofSeconds(30),
    ): List<S3ImageInfo> =
        listImageObjects(filePath)
            .map { it.toImageInfo(filePath, ttl) }
            .sortedByDescending { it.uploadedAt }
            .toList()

    private fun listImageObjects(filePath: String): Sequence<S3Object> =
        awsS3Client
            .getBucketListObjects(
                bucketName = awsProperties.s3.bucket,
                filePath = filePath,
            ).contents()
            .orEmpty()
            .asSequence()
            .filterNot { it.key().endsWith("/") }

    private fun S3Object.toImageInfo(
        filePath: String,
        ttl: Duration,
    ): S3ImageInfo {
        val fileName = key().substringAfterLast("/")

        return S3ImageInfo(
            url =
                awsS3Client.generateUrl(
                    bucketName = awsProperties.s3.bucket,
                    filePath = filePath,
                    fileName = fileName,
                    ttl = ttl,
                ),
            uploadedAt = LocalDateTime.ofInstant(lastModified(), SEOUL_ZONE_ID),
        )
    }
}
