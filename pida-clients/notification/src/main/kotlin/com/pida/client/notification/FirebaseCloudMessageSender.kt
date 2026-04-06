package com.pida.client.notification

import com.google.api.core.ApiFuture
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class FirebaseCloudMessageSender(
    private val firebaseMessagingProvider: FirebaseMessagingProvider,
    private val externalDependencyPolicy: ExternalDependencyPolicy,
) {
    companion object {
        private const val FCM_TIMEOUT_SECONDS = 3L
    }

    fun send(fcmSendRequest: FcmSendRequest): String? {
        val firebaseMessaging = firebaseMessagingProvider.getOrNull() ?: return unavailable()
        return externalDependencyPolicy.execute(ExternalDependency.FCM) {
            firebaseMessaging
                .sendAsync(toMessage(fcmSendRequest))
                .get(FCM_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }
    }

    fun sendAsync(requests: List<FcmSendRequest>): ApiFuture<BatchResponse>? {
        val firebaseMessaging = firebaseMessagingProvider.getOrNull() ?: return unavailable()
        return externalDependencyPolicy.execute(ExternalDependency.FCM) {
            val messages = requests.map { toMessage(it) }
            firebaseMessaging.sendEachAsync(messages)
        }
    }

    private fun toMessage(request: FcmSendRequest): Message =
        Message
            .builder()
            .setToken(request.fcmToken)
            .setNotification(
                Notification
                    .builder()
                    .setTitle(request.title)
                    .setBody(request.body)
                    .build(),
            ).setApnsConfig(
                ApnsConfig
                    .builder()
                    .putCustomData("destination", request.destination)
                    .setAps(
                        Aps
                            .builder()
                            .setAlert(request.body)
                            .setBadge(1)
                            .setSound("default")
                            .build(),
                    ).build(),
            ).build()

    fun sendEachForMulticastAll(
        title: String,
        body: String,
        destination: String,
        fcmTokens: List<String>,
    ): BatchResponse? {
        val firebaseMessaging = firebaseMessagingProvider.getOrNull() ?: return unavailable()
        val notification =
            Notification
                .builder()
                .setTitle(title)
                .setBody(body)
                .build()
        val message =
            MulticastMessage
                .builder()
                .setNotification(notification)
                .addAllTokens(fcmTokens)
                .setApnsConfig(
                    ApnsConfig
                        .builder()
                        .putCustomData("destination", destination)
                        .setAps(
                            Aps
                                .builder()
                                .setAlert(body)
                                .setBadge(1)
                                .setSound("default")
                                .build(),
                        ).build(),
                ).build()

        return externalDependencyPolicy.execute(ExternalDependency.FCM) {
            firebaseMessaging
                .sendEachForMulticastAsync(message)
                .get(FCM_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }
    }

    private fun <T> unavailable(): T? {
        externalDependencyPolicy.recordFallback(
            dependency = ExternalDependency.FCM,
            reason = "messaging-unavailable",
        )
        return null
    }
}
