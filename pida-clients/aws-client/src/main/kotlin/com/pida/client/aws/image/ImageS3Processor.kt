package com.pida.client.aws.image

import com.pida.client.aws.config.AwsProperties
import com.pida.client.aws.s3.AwsS3Client
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.S3ImageUrl
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ImageS3Processor(
    private val awsS3Client: AwsS3Client,
    private val awsProperties: AwsProperties,
    private val imageFileConstructor: ImageFileConstructor,
) : ImageS3Caller {

    override fun createUploadUrl(
        userId: Long,
        prefix: String,
        extension: String
    ): S3ImageUrl {
        val imageFilePath = imageFileConstructor.imageFilePath(userId, prefix)
        val imageFileName = imageFileConstructor.imageFileName(extension)

        val presignedUrl =
            awsS3Client.generateUploadUrl(
                awsProperties.s3.bucket,
                imageFilePath,
                imageFileName,
                Duration.ofSeconds(30),
            )

        return S3ImageUrl(
            presignedUrl,
            "${awsProperties.s3.imageOriginUrl}$imageFilePath/$imageFileName",
        )
    }
}
