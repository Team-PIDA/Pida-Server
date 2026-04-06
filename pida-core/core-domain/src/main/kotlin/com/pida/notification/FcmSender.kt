package com.pida.notification

import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class FcmSender(
    private val fcmRepository: FcmRepository,
    private val fcmMessageKeyGenerator: FcmMessageKeyGenerator,
) {
    private val logger by logger()

    fun sendAll(
        newMessages: Set<NewFirebaseCloudMessage>,
        maxTry: Int = 3,
    ): List<FirebaseCloudMessage> {
        if (newMessages.isEmpty()) return emptyList()

        val messages =
            newMessages
                .map {
                    FirebaseCloudMessage(
                        fcmKey = fcmMessageKeyGenerator.generateFcmKey(),
                        fcmToken = it.fcmToken,
                        title = it.title,
                        body = it.body,
                        destination = it.destination,
                        tryCount = 0,
                        sent = false,
                    )
                }.toSet()

        return sendWithRetries(messages, maxTry)
    }

    @Async
    fun sendAllAsync(
        newMessages: Set<NewFirebaseCloudMessage>,
        maxTry: Int = 3,
        callback: (List<FirebaseCloudMessage>) -> Unit = {},
    ) {
        val result = sendAll(newMessages, maxTry)
        callback(result)
    }

    private fun sendWithRetries(
        messages: Set<FirebaseCloudMessage>,
        maxTry: Int,
    ): List<FirebaseCloudMessage> {
        val sent = mutableSetOf<FirebaseCloudMessage>()
        val failed = mutableSetOf<FirebaseCloudMessage>()
        var pending = messages.toList()

        for (attempt in 1..maxTry) {
            if (pending.isEmpty()) break

            val result = fcmRepository.sendAll(pending)

            sent += result.filter { it.sent }
            failed += result.filter { !it.sent && it.tryCount >= maxTry }
            pending = result.filter { !it.sent && it.tryCount < maxTry }
        }

        return buildList {
            addAll(sent)
            addAll(failed)
            addAll(pending)
        }.also { results ->
            logger.info(
                "FCM delivery finished. total={}, sent={}, failed={}, pending={}",
                results.size,
                results.count { it.sent },
                results.count { !it.sent && it.tryCount >= maxTry },
                results.count { !it.sent && it.tryCount < maxTry },
            )
        }
    }
}
