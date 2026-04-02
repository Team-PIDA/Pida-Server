package com.pida.client.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.pida.support.extension.logger
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8

@Component
class FirebaseMessagingProvider(
    private val fcmProperties: FcmProperties,
    private val resourceLoader: ResourceLoader,
) {
    private val logger by logger()

    @Volatile
    private var firebaseMessaging: FirebaseMessaging? = null

    fun getOrNull(): FirebaseMessaging? {
        firebaseMessaging?.let { return it }

        synchronized(this) {
            firebaseMessaging?.let { return it }

            val keySource = fcmProperties.keyJson.trim()
            if (keySource.isEmpty()) {
                logger.warn("FCM credentials are empty. Push delivery will be disabled.")
                return null
            }

            return runCatching {
                val firebaseApp =
                    FirebaseApp.getApps().firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
                        ?: FirebaseApp.initializeApp(
                            FirebaseOptions
                                .builder()
                                .setCredentials(
                                    GoogleCredentials.fromStream(
                                        openCredentialsStream(keySource),
                                    ),
                                ).build(),
                            FirebaseApp.DEFAULT_APP_NAME,
                        )

                FirebaseMessaging.getInstance(firebaseApp).also {
                    firebaseMessaging = it
                }
            }.onFailure { error ->
                logger.error("Failed to initialize FirebaseMessaging lazily. Push delivery will be disabled.", error)
            }.getOrNull()
        }
    }

    private fun openCredentialsStream(keySource: String): InputStream =
        when {
            keySource.startsWith("classpath:") || keySource.startsWith("file:") -> {
                val resource = resourceLoader.getResource(keySource)
                require(resource.exists()) { "FCM credential resource does not exist: $keySource" }
                resource.inputStream
            }
            else -> {
                keySource.byteInputStream(UTF_8)
            }
        }
}
