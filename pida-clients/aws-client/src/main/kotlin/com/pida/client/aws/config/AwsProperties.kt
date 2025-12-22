package com.pida.client.aws.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws")
data class AwsProperties(
    val credentials: CredentialsProperties,
    val s3: S3Properties,
    val region: String,
    val endpoint: String?,
)

data class CredentialsProperties(
    val accessKey: String,
    val secretKey: String,
)

data class S3Properties(
    val bucket: String,
    val imageOriginUrl: String,
)
