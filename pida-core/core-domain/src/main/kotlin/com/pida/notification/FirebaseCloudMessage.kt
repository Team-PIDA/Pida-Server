package com.pida.notification

data class FirebaseCloudMessage(
    val fcmKey: FcmKey,
    val fcmToken: String,
    val title: String,
    val body: String,
    val tryCount: Int,
    val sent: Boolean,
)
