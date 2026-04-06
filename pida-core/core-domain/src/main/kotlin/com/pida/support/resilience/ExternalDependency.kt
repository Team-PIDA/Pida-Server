package com.pida.support.resilience

enum class ExternalDependency(
    val id: String,
) {
    KAKAO_MAP("kakao-map"),
    KAKAO_OAUTH("kakao-oauth"),
    APPLE_AUTH("apple-auth"),
    KMA_WEATHER("kma-weather"),
    AIR_KOREA("air-korea"),
    AWS_S3("aws-s3"),
    FCM("fcm"),
}
