package com.pida.client.notification

import com.pida.notification.FcmRepository
import com.pida.notification.FirebaseCloudMessage
import org.springframework.stereotype.Repository

@Repository
class FcmClientRepository(
    private val firebaseCloudMessageSender: FirebaseCloudMessageSender,
) : FcmRepository {
    override fun sendAll(firebaseCloudMessages: List<FirebaseCloudMessage>): List<FirebaseCloudMessage> {
        val fcmRequests =
            firebaseCloudMessages
                .map {
                    FcmSendRequest(
                        it.fcmToken,
                        it.title,
                        it.body,
                        it.destination,
                    )
                }.toList()

        val tokens = fcmRequests.map { it.fcmToken }
        val batchResult =
            firebaseCloudMessageSender.sendEachForMulticastAll(
                fcmRequests[0].title,
                fcmRequests[0].body,
                fcmRequests[0].destination,
                tokens,
            )

        val result =
            firebaseCloudMessages.mapIndexed { index, pushMessage ->
                FirebaseCloudMessage(
                    fcmKey = pushMessage.fcmKey,
                    fcmToken = pushMessage.fcmToken,
                    title = pushMessage.title,
                    body = pushMessage.body,
                    destination = pushMessage.destination,
                    tryCount = pushMessage.tryCount + 1,
                    sent = batchResult?.responses?.get(index)?.isSuccessful ?: false,
                )
            }
        return result
    }

    override fun send(firebaseCloudMessage: FirebaseCloudMessage): FirebaseCloudMessage {
        val request =
            FcmSendRequest(
                fcmToken = firebaseCloudMessage.fcmToken,
                title = firebaseCloudMessage.title,
                body = firebaseCloudMessage.body,
                destination = firebaseCloudMessage.destination,
            )
        val sendResult = firebaseCloudMessageSender.sendAsync(request).get()
        return FirebaseCloudMessage(
            fcmKey = firebaseCloudMessage.fcmKey,
            fcmToken = firebaseCloudMessage.fcmToken,
            title = firebaseCloudMessage.title,
            body = firebaseCloudMessage.body,
            destination = firebaseCloudMessage.destination,
            tryCount = firebaseCloudMessage.tryCount + 1,
            sent = sendResult.isNotBlank(),
        )
    }
}
