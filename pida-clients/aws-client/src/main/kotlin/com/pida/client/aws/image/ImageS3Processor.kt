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
            "${awsProperties.s3.imageOriginUrl}$imageFilePath/$imageFileName",
        )
    }
}
