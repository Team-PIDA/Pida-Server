package com.pida.client.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@Configuration
class FcmConfig(
    private val fcmProperties: FcmProperties,
) {
    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val keyJson = fcmProperties.keyJson
        if (keyJson.isEmpty()) {
            throw ErrorException(ErrorType.NOT_FOUND_FCM_CREDENTIALS)
        }

        if (FirebaseApp.getApps().isEmpty()) {
            val firebaseOptions =
                FirebaseOptions
                    .builder()
                    .setCredentials(
                        GoogleCredentials.fromStream(
                            ByteArrayInputStream(
                                keyJson.toByteArray(StandardCharsets.UTF_8),
                            ),
                        ),
                    ).build()
            FirebaseApp.initializeApp(firebaseOptions, FirebaseApp.DEFAULT_APP_NAME)
        }
        val firebaseApp = FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME)
        val firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)
        return firebaseMessaging
    }
}
