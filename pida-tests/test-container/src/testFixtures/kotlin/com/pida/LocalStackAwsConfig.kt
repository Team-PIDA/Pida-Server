package com.pida

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@TestConfiguration
class LocalStackAwsConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun localStackContainer(): LocalStackContainer =
        LocalStackContainer(
            DockerImageName.parse("localstack/localstack"),
        ).withServices(
            LocalStackContainer.Service.S3,
        )

    @Bean
    @Primary
    fun localStackCredentialProvider(localStackContainer: LocalStackContainer): AwsCredentialsProvider {
        val awsBasicCredentials =
            AwsBasicCredentials.create(
                localStackContainer.accessKey,
                localStackContainer.secretKey,
            )
        return StaticCredentialsProvider.create(awsBasicCredentials)
    }

    @Bean
    @Primary
    fun localStackS3Client(localStackContainer: LocalStackContainer): S3Client {
        val credentialsProvider = localStackCredentialProvider(localStackContainer)
        return S3Client
            .builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(localStackContainer.region))
            .endpointOverride(localStackContainer.endpoint)
            .build()
    }

    @Bean
    @Primary
    fun localStackS3Presigner(localStackContainer: LocalStackContainer): S3Presigner =
        S3Presigner
            .builder()
            .credentialsProvider(localStackCredentialProvider(localStackContainer))
            .region(Region.of(localStackContainer.region))
            .endpointOverride(localStackContainer.endpoint)
            .build()
}
