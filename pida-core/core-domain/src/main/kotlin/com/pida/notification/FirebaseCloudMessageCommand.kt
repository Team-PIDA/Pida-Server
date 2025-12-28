package com.pida.notification

data class FirebaseCloudMessageCommand(
    val fcmToken: String,
    val title: String,
    val body: String,
    val destination: String,
)
