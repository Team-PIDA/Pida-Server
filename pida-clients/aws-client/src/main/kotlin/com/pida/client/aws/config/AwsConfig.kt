package com.pida.client.aws.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI
import java.time.Duration

@Configuration
class AwsConfig(
    private val awsProperties: AwsProperties,
    private val env: Environment,
) {
    @Bean
    fun credentialProvider(): AwsCredentialsProvider =
        // AWS 인증 전략
        env
            .takeIf { "local" in it.activeProfiles }
            ?.let {
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsProperties.credentials.accessKey,
                        awsProperties.credentials.secretKey,
                    ),
                )
            }
            ?: DefaultCredentialsProvider.builder().build()

    @Bean(destroyMethod = "close") // 스프링 종료 시 커넥션 풀 정리
    fun s3Client(): S3Client {
        val connectionTimeout = Duration.ofMillis(awsProperties.connectionTimeout)
        val socketTimeout = Duration.ofMillis(awsProperties.socketTimeout)
        val client =
            S3Client
                .builder()
                .credentialsProvider(credentialProvider())
                .region(Region.of(awsProperties.region))
                .httpClient(
                    UrlConnectionHttpClient
                        .builder()
                        .connectionTimeout(connectionTimeout)
                        .socketTimeout(socketTimeout)
                        .build(),
                ).overrideConfiguration(
                    ClientOverrideConfiguration
                        .builder()
                        .apiCallAttemptTimeout(socketTimeout)
                        .apiCallTimeout(socketTimeout.plusMillis(500))
                        .build(),
                )
        awsProperties.endpoint?.let {
            client.endpointOverride(URI.create(awsProperties.endpoint))
        }

        return client.build()
    }

    @Bean(destroyMethod = "close")
    fun s3AsyncClient(): S3AsyncClient {
        val connectionTimeout = Duration.ofMillis(awsProperties.connectionTimeout)
        val socketTimeout = Duration.ofMillis(awsProperties.socketTimeout)
        val client =
            S3AsyncClient
                .builder()
                .credentialsProvider(credentialProvider())
                .region(Region.of(awsProperties.region))
                .httpClient(
                    NettyNioAsyncHttpClient
                        .builder()
                        .connectionTimeout(connectionTimeout)
                        .readTimeout(socketTimeout)
                        .build(),
                ).overrideConfiguration(
                    ClientOverrideConfiguration
                        .builder()
                        .apiCallAttemptTimeout(socketTimeout)
                        .apiCallTimeout(socketTimeout.plusMillis(500))
                        .build(),
                )
        awsProperties.endpoint?.let {
            client.endpointOverride(URI.create(awsProperties.endpoint))
        }

        return client.build()
    }

    @Bean(destroyMethod = "close")
    fun s3Presigner(): S3Presigner {
        val client =
            S3Presigner
                .builder()
                .credentialsProvider(credentialProvider())
                .region(Region.of(awsProperties.region))
        awsProperties.endpoint?.let {
            client.endpointOverride(URI.create(awsProperties.endpoint))
        }

        return client.build()
    }
}
