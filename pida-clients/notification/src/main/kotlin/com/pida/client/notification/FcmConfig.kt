package com.pida.client.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
class FcmConfig {
    @Value("classpath:pida-firebase-key.json")
    lateinit var firebaseKeyFile: Resource

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        if (FirebaseApp.getApps().isEmpty()) {
            val firebaseOptions =
                FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseKeyFile.inputStream))
                    .build()
            FirebaseApp.initializeApp(firebaseOptions, FirebaseApp.DEFAULT_APP_NAME)
        }
        val firebaseApp = FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME)
        val firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)
        return firebaseMessaging
    }
}
