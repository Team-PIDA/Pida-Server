package com.pida.notification

data class NewFirebaseCloudMessage(
    val fcmToken: String,
    val title: String,
    val body: String,
)
