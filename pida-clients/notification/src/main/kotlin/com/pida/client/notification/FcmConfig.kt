package com.pida.client.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FcmConfig(
    private val fcmProperties: FcmProperties,
) {
    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        if (FirebaseApp.getApps().isEmpty()) {
            val firebaseOptions =
                FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(fcmProperties.keyJson.byteInputStream()))
                    .build()
            FirebaseApp.initializeApp(firebaseOptions, FirebaseApp.DEFAULT_APP_NAME)
        }
        val firebaseApp = FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME)
        val firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)
        return firebaseMessaging
    }
}
