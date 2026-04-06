package com.pida.client.notification

import io.kotest.matchers.nulls.shouldBeNull
import org.junit.jupiter.api.Test
import org.springframework.core.io.DefaultResourceLoader

class FirebaseMessagingProviderTest {
    @Test
    fun `credentials가 비어 있으면 null을 반환하고 예외를 던지지 않는다`() {
        val provider =
            FirebaseMessagingProvider(
                fcmProperties = FcmProperties(keyJson = ""),
                resourceLoader = DefaultResourceLoader(),
            )

        provider.getOrNull().shouldBeNull()
    }

    @Test
    fun `존재하지 않는 resource 경로여도 null을 반환한다`() {
        val provider =
            FirebaseMessagingProvider(
                fcmProperties = FcmProperties(keyJson = "classpath:/missing-firebase.json"),
                resourceLoader = DefaultResourceLoader(),
            )

        provider.getOrNull().shouldBeNull()
    }
}
