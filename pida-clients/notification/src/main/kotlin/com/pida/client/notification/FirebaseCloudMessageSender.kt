package com.pida.client.notification

import com.google.api.core.ApiFuture
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Component
import java.util.concurrent.Future

@Component
class FirebaseCloudMessageSender(
    private val firebaseMessaging: FirebaseMessaging,
) {
    fun sendAsync(fcmSendRequest: FcmSendRequest): Future<String> = firebaseMessaging.sendAsync(toMessage(fcmSendRequest))

    fun sendAsync(requests: List<FcmSendRequest>): ApiFuture<BatchResponse> {
        val messages = requests.map { toMessage(it) }
        return firebaseMessaging.sendEachAsync(messages)
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
                    .setAps(
                        Aps
                            .builder()
                            .setAlert("${request.title}\n${request.body}")
                            .setBadge(1)
                            .setSound("default")
                            .build(),
                    ).build(),
            ).build()

    fun sendEachForMulticastAll(
        title: String,
        body: String,
        fcmTokens: List<String>,
    ): BatchResponse? {
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
                        .setAps(
                            Aps
                                .builder()
                                .setAlert("$title\n$body}")
                                .setBadge(1)
                                .setSound("default")
                                .build(),
                        ).build(),
                ).build()

        return firebaseMessaging.sendEachForMulticast(message)
    }
}
