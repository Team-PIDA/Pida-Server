package com.pida.client.notification

data class FcmSendRequest(
    val fcmToken: String,
    val title: String,
    val body: String,
)
