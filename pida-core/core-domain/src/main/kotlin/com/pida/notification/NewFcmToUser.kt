package com.pida.notification

data class NewFcmToUser(
    val userKey: String,
    val title: String,
    val body: String,
)
