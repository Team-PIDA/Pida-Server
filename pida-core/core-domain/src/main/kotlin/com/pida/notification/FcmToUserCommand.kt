package com.pida.notification

data class FcmToUserCommand(
    val userKey: String,
    val title: String,
    val body: String,
)
